package com.indexdata.pz2utils4jsf.exceptions;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

public class CustomExceptionHandler extends ExceptionHandlerWrapper {

  private ExceptionHandler wrapped;
  
  public CustomExceptionHandler (ExceptionHandler wrapped) {
    this.wrapped = wrapped;
  }
    
  @Override
  public ExceptionHandler getWrapped() {
    return this.wrapped;
  }

  @Override
  public void handle() throws FacesException {
    for (Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator(); i.hasNext();) {
      ExceptionQueuedEvent event = i.next();
      ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();
      Throwable t = context.getException();
      if (t instanceof ViewExpiredException) {
        // ViewExpiredException vee = (ViewExpiredException) t;
        FacesContext fc = FacesContext.getCurrentInstance();
        // Map<String, Object> requestMap = fc.getExternalContext().getRequestMap();
        NavigationHandler nav = fc.getApplication().getNavigationHandler();
        context.getContext().getPartialViewContext().setRenderAll(true);        
        try {
          nav.handleNavigation(fc, null, "search");
          fc.renderResponse();
        } finally {
          i.remove();
        }
      }
    }
    getWrapped().handle();
  }
}
