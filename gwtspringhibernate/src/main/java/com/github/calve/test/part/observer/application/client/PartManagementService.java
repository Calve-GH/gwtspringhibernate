package com.github.calve.test.part.observer.application.client;

import com.github.calve.test.part.observer.application.shared.PartDto;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;

import java.util.List;

@RemoteServiceRelativePath("part/partManagementService.gwt")
public interface PartManagementService extends RemoteService {

    PagingLoadResult<PartDto> loadObjects(FilterPagingLoadConfig config);

    String refreshRequiredSet();

    void deletePart(PartDto partDto);

    void updateServerData(List<PartDto> list);

}
