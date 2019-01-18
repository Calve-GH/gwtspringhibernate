package com.github.calve.test.part.observer.application.server;

import com.github.calve.test.part.observer.application.server.entity.Part;
import com.github.calve.test.part.observer.application.shared.PartDto;
import com.github.calve.test.part.observer.application.client.PartManagementService;
import com.github.calve.test.part.observer.application.server.dao.PartDao;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("partManagementService")
public class PartManagementServiceImpl implements PartManagementService {
	@Autowired
	private PartDao partDao;
    @Autowired
    private ModelMapper modelMapper;

    private List<PartDto> parts;

    private int getMinimumRequiredSet() {
        if (parts != null && parts.size() != 0)
            return parts.stream().filter(PartDto::isRequired).mapToInt(PartDto::getQuantity).min().orElse(0);
        return 0;
    }

    @Override
    public String refreshRequiredSet() {
        return Integer.toString(getMinimumRequiredSet());
    }

    @Override
    public void deletePart(PartDto partDto) {
        partDao.deletePart(convertToEntity(partDto));
        parts.remove(partDto);
    }

    @Override
    public void updateServerData(List<PartDto> list) {
        for (PartDto dto : list) {
            if (dto.getId() < 0) {
                parts.add(insertPart(dto));
                continue;
            }
            if (parts.contains(dto)) {
                continue;
            }
            updatePart(dto);
            for (PartDto dto1 : parts) {
                if ( dto1.getId() == dto.getId()) {
                    parts.set(parts.indexOf(dto1), dto);
                    break;
                }
            }
        }
    }

    private PartDto insertPart(PartDto part) {
        part.setId(partDao.savePart(convertToEntity(part)));
        return part;
    }

    private void updatePart(PartDto part) {
        partDao.updatePart(convertToEntity(part));
    }

    private void loadPartsDto() {
        parts = convertEntityToList(partDao.getAll());
    }

    @Override
    public PagingLoadResult<PartDto> loadObjects(FilterPagingLoadConfig config) {
        if (parts == null) {
            loadPartsDto();
        }

        ArrayList<PartDto> temp = new ArrayList<>();
        ArrayList<PartDto> remove = new ArrayList<>();
        temp.addAll(parts);

        sortSuitableObjects(config, temp);
        selectNoSuitableObjects(config, remove);

        for (PartDto s : remove) {
            temp.remove(s);
        }

        ArrayList<PartDto> sublist = fillSortedList(config, temp);

        return new PagingLoadResultBean<>(sublist, temp.size(), config.getOffset());
    }

    private ArrayList<PartDto> fillSortedList(FilterPagingLoadConfig config, ArrayList<PartDto> temp) {
        ArrayList<PartDto> sublist = new ArrayList<>();
        int start = config.getOffset();
        int limit = temp.size();
        if (config.getLimit() > 0) {
            limit = Math.min(start + config.getLimit(), limit);
        }
        for (int i = config.getOffset(); i < limit; i++) {
            sublist.add(temp.get(i));
        }
        return sublist;
    }

    private void selectNoSuitableObjects(FilterPagingLoadConfig config, ArrayList<PartDto> remove) {
        List<FilterConfig> filters = config.getFilters();
        for (FilterConfig f : filters) {
            String type = f.getType();
            String test = f.getValue();
            String path = f.getField();
            String comparison = f.getComparison();

            String safeTest = test == null ? "" : test;

            for (PartDto s : parts) {
                String value = getPartValue(s, path);

                if (safeTest.length() == 0 && (value == null || value.length() == 0)) {
                    continue;
                } else if (value == null) {
                    remove.add(s);
                    continue;
                }

                if ("string".equals(type)) {
                    if (!value.toLowerCase().contains(safeTest.toLowerCase())) {
                        remove.add(s);
                    }
                } else if ("boolean".equals(type)) {
                    if (isBooleanFiltered(safeTest, value)) {
                        remove.add(s);
                    }
                } else if ("numeric".equals(type)) {
                    if (isNumberFiltered(safeTest, comparison, value)) {
                        remove.add(s);
                    }
                }
            }
        }
    }

    private void sortSuitableObjects(FilterPagingLoadConfig config, ArrayList<PartDto> temp) {
        if (config.getSortInfo().size() > 0) {
            SortInfo sort = config.getSortInfo().get(0);
            if (sort.getSortField() != null) {
                final String sortField = sort.getSortField();
                if (sortField != null) {
                    Collections.sort(temp, sort.getSortDir().comparator(getSuitableComparator(sortField)));
                }
            }
        }
    }

    private Comparator<PartDto> getSuitableComparator(final String sortField) {
        return new Comparator<PartDto>() {
            @Override
            public int compare(PartDto p1, PartDto p2) {
                switch (sortField) {
                    case "name":
                        return p1.getName().compareTo(p2.getName());
                    case "quantity":
                        return p1.getQuantity().compareTo(p2.getQuantity());
                    case "required":
                        Boolean b1 = p1.isRequired();
                        Boolean b2 = p2.isRequired();
                        return b1.compareTo(b2);
                }
                return 0;
            }
        };
    }

    private String getPartValue(PartDto s, String property) {
        switch (property) {
            case "name":
                return s.getName();
            case "quantity":
                return String.valueOf(s.getQuantity());
            case "required":
                return String.valueOf(s.isRequired());
        }
        return "";
    }

    private boolean isBooleanFiltered(String test, String value) {
        if (value == null) {
            return true;
        }
        boolean t = Boolean.valueOf(test);
        boolean v = Boolean.parseBoolean(value);

        return t != v;
    }

    private boolean isNumberFiltered(String test, String comparison, String value) {
        if (value == null) {
            return false;
        }
        double t = Double.valueOf(test);
        double v = Double.valueOf(value);

        if ("gt".equals(comparison)) {
            return t >= v;
        } else if ("lt".equals(comparison)) {
            return t <= v;
        } else if ("eq".equals(comparison)) {
            return t != v;
        }
        return false;
    }

    private PartDto convertToDto(Part part) {
        return modelMapper.map(part, PartDto.class);
    }

    private Part convertToEntity(PartDto partDto) {
        return modelMapper.map(partDto, Part.class);
    }

    private List<PartDto> convertEntityToList(List<Part> entityList) {
        List<PartDto> result = new ArrayList<>();
        for (Part part : entityList)
            result.add(convertToDto(part));
        return result;
    }
}