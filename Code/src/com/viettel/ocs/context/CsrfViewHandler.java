package com.viettel.ocs.context;

import java.io.IOException;
import java.util.UUID;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

public class CsrfViewHandler extends ViewHandlerWrapper {

    private static final String CSRF_TOKEN_KEY = CsrfViewHandler.class.getName();

    private ViewHandler wrapped;

    public CsrfViewHandler(ViewHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public UIViewRoot restoreView(FacesContext context, String viewId) {
        UIViewRoot view = super.restoreView(context, viewId);
        return getCsrfToken(context).equals(view.getAttributes().get(CSRF_TOKEN_KEY)) ? view : null;
    }

    @Override
    public void renderView(FacesContext context, UIViewRoot view) throws IOException, FacesException {
        view.getAttributes().put(CSRF_TOKEN_KEY, getCsrfToken(context));
        super.renderView(context, view);
    }

    private String getCsrfToken(FacesContext context) {
        String csrfToken = (String) context.getExternalContext().getSessionMap().get(CSRF_TOKEN_KEY);

        if (csrfToken == null) {
            csrfToken = UUID.randomUUID().toString();
            context.getExternalContext().getSessionMap().put(CSRF_TOKEN_KEY, csrfToken);
        }

        return csrfToken;
    }

    @Override
    public ViewHandler getWrapped() {
        return wrapped;
    }

}