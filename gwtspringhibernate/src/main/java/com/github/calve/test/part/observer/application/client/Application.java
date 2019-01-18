package com.github.calve.test.part.observer.application.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.*;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Application implements EntryPoint {
    public void onModuleLoad() {
        PagingGrid pge = new PagingGrid();
        pge.asWidget().setPixelSize(550, 415);
        RootPanel.get("ItemList").add(pge);
    }
}
