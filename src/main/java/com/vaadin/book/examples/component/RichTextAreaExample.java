package com.vaadin.book.examples.component;

import com.vaadin.book.examples.BookExampleBundle;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class RichTextAreaExample extends CustomComponent implements BookExampleBundle {
    private static final long serialVersionUID = -4292553844521293140L;

    public void init (String context) {
        VerticalLayout layout = new VerticalLayout();
        
        if ("basic".equals(context))
            basic(layout);
        else if ("localization".equals(context))
            localization(layout);
        
        setCompositionRoot(layout);
    }
     
    void basic(VerticalLayout layout) {
        // BEGIN-EXAMPLE: component.richtextarea.basic
        final RichTextArea area = new RichTextArea();
        area.setValue("<h1>Helpful Heading</h1>"+
                      "<p>All this is for you to edit.</p>");
        
        // Handle edits
        Button ok = new Button("OK");
        final Label feedback = new Label((String) area.getValue()); 
        ok.addListener(new ClickListener() {
            private static final long serialVersionUID = 7413250032073775204L;

            public void buttonClick(ClickEvent event) {
                feedback.setValue(area.getValue());
            }
        });
        
        // END-EXAMPLE: component.richtextarea.basic

        layout.addComponent(area);
        layout.addComponent(ok);
        layout.addComponent(feedback);
    }
    
    void localization(VerticalLayout layout) {
        // BEGIN-EXAMPLE: component.richtextarea.localization
        RichTextArea area = new RichTextArea();
        area.setCaption("You can edit stuff here");
        area.setValue("<h1>Helpful Heading</h1>"+
                      "<p>All this is for you to edit.</p>");
        
        // END-EXAMPLE: component.richtextarea.localization

        layout.addComponent(area);
    }
    
}
