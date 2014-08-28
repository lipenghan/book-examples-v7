package com.vaadin.book.examples.application;

import com.vaadin.book.examples.BookExampleBundle;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.WrappedSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class LifecycleExample extends CustomComponent implements BookExampleBundle {
    private static final long serialVersionUID = -4292553844521293140L;
    String context;
    
    public void init(String context) {
        this.context = context;
        setCompositionRoot(new Label("Dummy"));
    }
    
    public void attach() {
        VerticalLayout layout = new VerticalLayout();
        
        if ("expiration".equals(context))
            expiration(layout);
        else if ("closing".equals(context))
            closing(layout);

        setCompositionRoot(layout);
    }
    
    @SuppressWarnings("serial")
    void expiration(Layout layout) {
        // BEGIN-EXAMPLE: application.lifecycle.expiration
        
        // Obtain the wrapped HttpSession/PortletSession
        final WrappedSession lowlevelSession = getSession().getSession();

        // Display the HttpSession configuration settings
        Panel httpSessionPanel = new Panel("Http-/PortletSession Parameters");
        FormLayout form1 = new FormLayout();
        form1.addComponent(new Label() {{
            setCaption("Server session timeout (minutes)");
            setValue(String.valueOf(lowlevelSession.getMaxInactiveInterval()));
        }});
        httpSessionPanel.setContent(form1);
        
        // Obtain the deployment configuration for the session
        final DeploymentConfiguration conf =
                getSession().getConfiguration();
        
        // Display the VaadinSession configuration settings
        Panel vaadinSessionPanel = new Panel("VaadinSession Parameters");
        FormLayout form2 = new FormLayout();
        form2.addComponent(new Label() {{
            setCaption("Heartbeat Interval (s)");
            setValue(String.valueOf(conf.getHeartbeatInterval()));
        }});
        form2.addComponent(new Label() {{
            setCaption("Are idle sessions closed?");
            setValue(String.valueOf(conf.isCloseIdleSessions()));
        }});
        form2.addComponent(new Label() {{
            setCaption("Is XSRF Protection enabled?");
            setValue(String.valueOf(conf.isXsrfProtectionEnabled()));
        }});
        form2.addComponent(new Label() {{
            setCaption("Is the servlet in production mode?");
            setValue(String.valueOf(conf.isProductionMode()));
        }});
        form2.addComponent(new Label() {{
            setCaption("Resource Caching Time");
            setValue(String.valueOf(conf.getResourceCacheTime()));
        }});
        vaadinSessionPanel.setContent(form2);
        // END-EXAMPLE: application.lifecycle.expiration
        
        layout.addComponent(httpSessionPanel);
        layout.addComponent(vaadinSessionPanel);
    }

    public static final String closeDescription =
            "<h1>Closing a Session</h1>" +
            "<p></p>";
    
    void closing(Layout layout) {
        // BEGIN-EXAMPLE: application.lifecycle.closing
        Button logout = new Button("Logout");
        logout.addClickListener(new Button.ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                // Redirect from the page
                getUI().getPage().setLocation(
                    "/book-examples-vaadin7/closed.html");
                
                // Close the VaadinSession
                getSession().close();
            }
        });
        // END-EXAMPLE: application.lifecycle.closing
        layout.addComponent(logout);
    }
}
