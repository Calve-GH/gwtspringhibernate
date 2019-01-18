package com.github.calve.test.part.observer.application.client;

import com.github.calve.test.part.observer.application.shared.PartDto;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;

public class PagingLoaderCreator {

	public static PagingLoader<FilterPagingLoadConfig, PagingLoadResult<PartDto>> getLoader(PartManagementServiceAsync service) {

		RpcProxy<FilterPagingLoadConfig, PagingLoadResult<PartDto>> rpxProxy = new RpcProxy<FilterPagingLoadConfig, PagingLoadResult<PartDto>>() {
			@Override
			public void load(FilterPagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<PartDto>> callback) {
				service.loadObjects(loadConfig, callback);
			}
		};
		
		// Paging Filter Loader
		final PagingLoader<FilterPagingLoadConfig, PagingLoadResult<PartDto>> loader = new PagingLoader<FilterPagingLoadConfig, PagingLoadResult<PartDto>>(
				rpxProxy);
		return loader;
	}
}
