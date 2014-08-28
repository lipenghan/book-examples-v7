package com.vaadin.book.examples.addons.touchkit;

// BEGIN-EXAMPLE: mobile.overview.phone
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.vaadin.addon.touchkit.extensions.LocalStorage;
import com.vaadin.addon.touchkit.extensions.LocalStorageCallback;
import com.vaadin.addon.touchkit.ui.EmailField;
import com.vaadin.addon.touchkit.ui.HorizontalButtonGroup;
import com.vaadin.addon.touchkit.ui.NavigationButton;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickEvent;
import com.vaadin.addon.touchkit.ui.NavigationButton.NavigationButtonClickListener;
import com.vaadin.addon.touchkit.ui.NavigationManager;
import com.vaadin.addon.touchkit.ui.NavigationManager.NavigationEvent;
import com.vaadin.addon.touchkit.ui.NavigationManager.NavigationListener;
import com.vaadin.addon.touchkit.ui.NavigationView;
import com.vaadin.addon.touchkit.ui.NumberField;
import com.vaadin.addon.touchkit.ui.Popover;
import com.vaadin.addon.touchkit.ui.SwipeView;
import com.vaadin.addon.touchkit.ui.Switch;
import com.vaadin.addon.touchkit.ui.TabBarView;
import com.vaadin.addon.touchkit.ui.UrlField;
import com.vaadin.addon.touchkit.ui.VerticalComponentGroup;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;

@Theme("mobiletheme")
@Title("TouchKit Components")
@Widgetset("com.vaadin.book.examples.addons.touchkit.MyMobileWidgetSet")
public class TouchKitComponentsUI extends UI {
    private static final long serialVersionUID = 511085335415683713L;

    class MainView extends NavigationView {
        private static final long serialVersionUID = -7970887918515739005L;

        public MainView() {
            super("Main View");
            CssLayout content = new CssLayout();

            VerticalComponentGroup group =
                new VerticalComponentGroup("TouchKit Components");
            group.setWidth("100%");
            
            // Navigation to sub-views
            
            // Get the button caption for the view caption
            group.addComponent(new NavigationButton(new PopoverView()));

            // Set the button caption explicitly
            group.addComponent(new NavigationButton("Decorated Popover",
                                   new DecoratedPopoverView()));

            NavigationButton toNavButton = new NavigationButton(new NaviButtonView());
            toNavButton.setDescription("New");
            toNavButton.addStyleName("pill");
            group.addComponent(toNavButton);
            
            // Override default target view caption
            NavigationButton toSwitch = new NavigationButton("Switch");
            toSwitch.setTargetView(new SwitchView());
            toSwitch.setTargetViewCaption("Switch toggle");
            group.addComponent(toSwitch);

            // Create the view object dynamically
            final NavigationButton fieldViewButton = new NavigationButton();
            fieldViewButton.setTargetViewCaption("Text Input Fields");
            fieldViewButton.addClickListener(
                new NavigationButtonClickListener() {
                private static final long serialVersionUID = 5744966310815951435L;

                @Override
                public void buttonClick(NavigationButtonClickEvent event) {
                    fieldViewButton.getNavigationManager()
                        .navigateTo(new FieldView());
                }
            });
            group.addComponent(fieldViewButton);
            
            group.addComponent(new NavigationButton(new HBGView()));

            content.addComponent(group);

            VerticalComponentGroup features =
                    new VerticalComponentGroup("Features");
            features.addComponent(new NavigationButton(new LocalStorageView()));
            features.addComponent(new NavigationButton(new UploadView()));
            features.addComponent(new NavigationButton(new ImageUploadView()));
            features.addComponent(new NavigationButton(new ThemeingView()));
            features.addComponent(new NavigationButton(new DownloadView()));
            content.addComponent(features);

            setContent(content);
        }
        
        @Override
        protected void onBecomingVisible() {
            super.onBecomingVisible();
        }
    }
    
    class PopoverView extends NavigationView {
        private static final long serialVersionUID = 3750679255269899661L;

        Table table = new Table("Planets", planetData());

        public PopoverView() {
            super("Popover");

            CssLayout content = new CssLayout();

            content.addComponent(new Label("This view demonstrates the Popover component"));
            table.setWidth("100%");
            table.setPageLength(table.size());
            content.addComponent(table);
            
            table.setSelectable(true);
            table.setImmediate(true);
            table.addItemClickListener(new ItemClickListener() {
                private static final long serialVersionUID = -6439972483179810841L;

                @SuppressWarnings("unchecked")
                @Override
                public void itemClick(ItemClickEvent event) {
                    final Object itemId = event.getItemId();
                    
                    class DetailsPopover extends Popover
                          implements MouseEvents.ClickListener {
                        private static final long serialVersionUID = 3742410675843716898L;

                        public DetailsPopover() {
                            setWidth("350px");
                            setHeight("65%");

                            // Have some details to display
                            FormLayout layout = new FormLayout();
                            for (String pid: (Collection<String>) table.getContainerPropertyIds()) {
                                Label label = new Label(table.getContainerProperty(itemId, pid));
                                label.setCaption(table.getColumnHeader(pid));
                                layout.addComponent(label);
                            }
                            layout.setMargin(true);

                            NavigationView content = new NavigationView(layout);
                            content.setCaption("Details");
                            setContent(content);

                            addClickListener(this);
                        }

                        @Override
                        public void click(MouseEvents.ClickEvent event) {
                            close();
                        }
                    }

                    DetailsPopover popover = new DetailsPopover();
                    popover.showRelativeTo(getNavigationBar());
                }
            });
            
            setContent(content);
        }

        protected void onBecomingVisible() {
            super.onBecomingVisible();
            
            table.select(null);
        }
    }

    class DecoratedPopoverView extends NavigationView {
        private static final long serialVersionUID = 3750679255269899661L;

        Table table = new Table("Planets", planetData());

        public DecoratedPopoverView() {
            super("Decorated Popover View");
            
            CssLayout content = new CssLayout();

            content.addComponent(new Label("This view demonstrates the Popover component"));
            table.setWidth("100%");
            table.setPageLength(table.size());
            content.addComponent(table);
            
            table.setSelectable(true);
            table.setImmediate(true);
            table.addItemClickListener(new ItemClickListener() {
                private static final long serialVersionUID = -6439972483179810841L;

                @SuppressWarnings("unchecked")
                @Override
                public void itemClick(ItemClickEvent event) {
                    Object itemId = event.getItemId();
                    
                    // Show the details in a decorated Popover
                    class DetailsPopover extends Popover
                          implements Button.ClickListener {
                        private static final long serialVersionUID = 1L;

                        public DetailsPopover(Table table, Object itemId) {
                            setWidth("350px");
                            setHeight("65%");
                            
                            // Have some details to display
                            FormLayout layout = new FormLayout();
                            for (String pid: (Collection<String>) table.getContainerPropertyIds()) {
                                Label label = new Label(table.getContainerProperty(itemId, pid));
                                label.setCaption(table.getColumnHeader(pid));
                                layout.addComponent(label);
                            }
                            layout.setMargin(true);

                            // Decorate with a navigation view
                            NavigationView content = new NavigationView(layout);
                            content.setCaption("Details");
                            setContent(content);

                            // Have a close button
                            Button close = new Button(null, this);
                            close.setStyleName("close-popover");
                            close.setIcon(new ThemeResource("close64.png"));
                            content.setRightComponent(close);
                        }

                        public void buttonClick(ClickEvent event) {
                            close();
                        }
                    }

                    final DetailsPopover popover = new DetailsPopover(table, itemId);
                    popover.showRelativeTo(getNavigationBar());
                }
            });
            
            setContent(content);
        }

        protected void onBecomingVisible() {
            super.onBecomingVisible();
            
            table.select(null);
        }
    }
    
    class NaviButtonView extends NavigationView {
        private static final long serialVersionUID = -310203104553728231L;
        
        public NaviButtonView() {
            super("Navigation Button");
        }

        protected void onBecomingVisible() {
            super.onBecomingVisible();

            class PlanetDetails extends NavigationView {
                BeanItem<Planet> planetItem;
                public PlanetDetails(BeanItem<Planet> planetItem) {
                    super(planetItem.getBean().getName());
                    this.planetItem = planetItem;
                }
                
                @Override
                protected void onBecomingVisible() {
                    super.onBecomingVisible();

                    VerticalComponentGroup group =
                            new VerticalComponentGroup();
                    
                    Planet planet = planetItem.getBean();
                    group.addComponent(new Image(null, new ThemeResource(
                        "../book-examples/img/planets/" +
                        planet.getName() + ".jpg")));
                    group.addComponent(new TextField("Name",
                        planetItem.getItemProperty("name")));
                    group.addComponent(new TextField("Orbit",
                        planetItem.getItemProperty("orbitRadius")));
                    group.addComponent(new TextField("Moons",
                        planetItem.getItemProperty("moons")));
                    
                    setContent(group);
                }
            }
            
            VerticalComponentGroup group =
                    new VerticalComponentGroup();

            final BeanItemContainer<Planet> planets = planetData();
            for (final Planet planet: planets.getItemIds()) {
                final NavigationButton planetNav =
                        new NavigationButton(planet.getName());
                planetNav.setTargetViewCaption(planet.getName());
                planetNav.setIcon(new ThemeResource(
                    "../book-examples/img/planets/" + planet.getName() + "_symbol.png"));
                if (planet.getMoons() > 0) {
                    planetNav.setDescription(planet.getMoons() + " moons");
                    if (planet.getMoons() == 1)
                        planetNav.addStyleName("emphasis");
                    if (planet.getMoons() == 2)
                        planetNav.addStyleName("pill");
                }

                // Create view dynamically
                planetNav.addClickListener(
                    new NavigationButtonClickListener() {
                    private static final long serialVersionUID = -8172534021981013020L;

                    @Override
                    public void buttonClick(NavigationButtonClickEvent event) {
                        planetNav.getNavigationManager().
                            navigateTo(new PlanetDetails(planets.getItem(planet)));
                    }
                });
                
                group.addComponent(planetNav);
            }
            
            setContent(group);
        }
    }    
    
    class SwitchView extends NavigationView {
        private static final long serialVersionUID = -310203104553728231L;

        protected void onBecomingVisible() {
            super.onBecomingVisible();
            setCaption("Switch toggle");
            
            VerticalComponentGroup group =
                    new VerticalComponentGroup();
            Switch myswitch = new Switch("To be or not to be?");
            myswitch.setValue(true);
            group.addComponent(myswitch);
            setContent(group);
        }
    }    
    
    class FieldView extends NavigationView {
        private static final long serialVersionUID = -310203104553728231L;
        
        public FieldView() {
            super("Text Input Fields");
        }

        protected void onBecomingVisible() {
            super.onBecomingVisible();
            
            class MyForm extends CssLayout
                  implements Button.ClickListener {
                private static final long serialVersionUID = 8512115834680760052L;

                EmailField  email  = new EmailField("Email");
                NumberField number = new NumberField("Number");
                UrlField    url    = new UrlField("URL");
                FieldGroup binder;

                public MyForm(Item item) {
                    VerticalComponentGroup group =
                            new VerticalComponentGroup();
                    FormLayout formLayout = new FormLayout();

                    email.addValidator(new EmailValidator(
                        "Not a proper email address"));
                    formLayout.addComponent(email);
                    
                    number.addValidator(new IntegerRangeValidator(
                        "Outside range", 1, 1000));
                    formLayout.addComponent(number);

                    formLayout.addComponent(url);
                    
                    group.addComponent(formLayout);
                    addComponent(group);

                    // Bind the form
                    binder = new FieldGroup(item);
                    binder.bindMemberFields(this);
                    
                    // Handle the form
                    Button commit = new Button("OK", this);
                    addComponent(commit);
                }

                @Override
                public void buttonClick(ClickEvent event) {
                    try {
                        binder.commit();
                    } catch (CommitException e) {
                        Notification.show("Invalid value");
                    }
                }
            }
            
            Item item = new PropertysetItem();
            item.addItemProperty("email",
                new ObjectProperty<String>(""));
            item.addItemProperty("number",
                new ObjectProperty<Integer>(0));
            item.addItemProperty("url",
                new ObjectProperty<String>(""));
            
            MyForm form = new MyForm(item);
            setContent(form);
        }
    }    

    class HBGView extends NavigationView {
        private static final long serialVersionUID = -310203104553728231L;

        public HBGView() {
            super("Horizontal Button Group");
        }

        protected void onBecomingVisible() {
            super.onBecomingVisible();

            VerticalComponentGroup vertical =
                    new VerticalComponentGroup();
            vertical.addComponent(new TextField("Name"));

            HorizontalButtonGroup buttons =
                    new HorizontalButtonGroup();
            buttons.addComponent(new Button("OK"));
            buttons.addComponent(new Button("Cancel"));
            vertical.addComponent(buttons);

            setContent(vertical);
        }
    }    
    
    class LocalStorageView extends NavigationView {
        private static final long serialVersionUID = -310203104553728231L;

        public LocalStorageView() {
            super("Local Storage");
        }

        protected void onBecomingVisible() {
            super.onBecomingVisible();
            
            final VerticalComponentGroup vertical =
                    new VerticalComponentGroup();
            final TextField valueEditor = new TextField("Value");
            valueEditor.setNullRepresentation("");
            vertical.addComponent(valueEditor);

            final LocalStorage storage = LocalStorage.get();
            storage.get("value", new LocalStorageCallback() {
                private static final long serialVersionUID = 7842668649729234419L;

                @Override
                public void onSuccess(String value) {
                    valueEditor.setValue(value);
                    Notification.show("Value Retrieved");
                }

                @Override
                public void onFailure(FailureEvent error) {
                    Notification.show("Failed because: " +
                        error.getMessage());
                }
            });

            HorizontalButtonGroup buttons =
                    new HorizontalButtonGroup();
            buttons.addComponent(new Button("Store", new ClickListener() {
                private static final long serialVersionUID = 5477613264771189859L;

                @Override
                public void buttonClick(ClickEvent event) {
                    storage.put("value", valueEditor.getValue(),
                                new LocalStorageCallback() {
                        private static final long serialVersionUID = 5999537256431186976L;

                        @Override
                        public void onSuccess(String value) {
                            Notification.show("Stored");
                        }
                        
                        @Override
                        public void onFailure(FailureEvent error) {
                            Notification.show("Storing Failed");
                        }
                    });
                }
            }));
            vertical.addComponent(buttons);

            setContent(vertical);
        }
    }    

    class UploadView extends NavigationView {
        private static final long serialVersionUID = -310203104553728231L;

        public UploadView() {
            super("Regular Upload");
        }

        protected void onBecomingVisible() {
            super.onBecomingVisible();
            
            final VerticalComponentGroup layout =
                    new VerticalComponentGroup();

            // Display the image - only a placeholder first
            final Image image = new Image();
            image.setWidth("100%");
            image.setVisible(false);
            layout.addComponent(image);

            // Implement both receiver that saves upload in a file and
            // listener for successful upload
            class ImageUploader implements Receiver, SucceededListener,
                                           ProgressListener {
                private static final long serialVersionUID = 1L;

                final static int maxLength = 10000000;
                ByteArrayOutputStream fos = null;
                String filename;
                Upload upload;
                
                public ImageUploader(Upload upload) {
                    this.upload = upload;
                }
             
                public OutputStream receiveUpload(String filename,
                                                  String mimeType) {
                    this.filename = filename;
                    fos = new ByteArrayOutputStream(maxLength + 1);
                    return fos; // Return the output stream to write to
                }

                @Override
                public void updateProgress(long readBytes,
                                           long contentLength) {
                    if (readBytes > maxLength) {
                        Notification.show("Too big content");
                        upload.interruptUpload();
                    }
                }

                public void uploadSucceeded(SucceededEvent event) {
                    // Show the uploaded file in the image viewer
                    image.setSource(new StreamResource(new StreamSource() {
                        private static final long serialVersionUID = 7063779452227893200L;

                        @Override
                        public InputStream getStream() {
                            byte[] bytes = fos.toByteArray();
                            return new ByteArrayInputStream(bytes);
                        }
                    }, filename));
                    
                    image.setVisible(true);
                }
            };

            Upload upload = new Upload();
            ImageUploader uploader = new ImageUploader(upload);
            upload.setReceiver(uploader);
            upload.addSucceededListener(uploader);
            upload.setImmediate(true); // Only button

            HorizontalButtonGroup group = new HorizontalButtonGroup();
            group.addComponent(upload);
            layout.addComponent(group);
            
            setContent(layout);
        }
    }

    class ImageUploadView extends NavigationView {
        private static final long serialVersionUID = -310203104553728231L;

        public ImageUploadView() {
            super("Image Upload");
        }

        protected void onBecomingVisible() {
            super.onBecomingVisible();
            
            final VerticalComponentGroup layout =
                    new VerticalComponentGroup();

            // Display the image - only a placeholder first
            final Image image = new Image();
            image.setWidth("100%");
            image.setVisible(false);
            layout.addComponent(image);

            // Implement both receiver that saves upload in a file and
            // listener for successful upload
            class ImageUploader implements Receiver, SucceededListener,
                                           ProgressListener {
                private static final long serialVersionUID = 1L;

                final static int maxLength = 10000000;
                ByteArrayOutputStream fos = null;
                String filename;
                Upload upload;
                
                public ImageUploader(Upload upload) {
                    this.upload = upload;
                }
             
                public OutputStream receiveUpload(String filename,
                                                  String mimeType) {
                    this.filename = filename;
                    fos = new ByteArrayOutputStream(maxLength + 1);
                    return fos; // Return the output stream to write to
                }

                @Override
                public void updateProgress(long readBytes,
                                           long contentLength) {
                    if (readBytes > maxLength) {
                        Notification.show("Too big content");
                        upload.interruptUpload();
                    }
                }

                public void uploadSucceeded(SucceededEvent event) {
                    // Show the uploaded file in the image viewer
                    image.setSource(new StreamResource(new StreamSource() {
                        private static final long serialVersionUID = 7063779452227893200L;

                        @Override
                        public InputStream getStream() {
                            byte[] bytes = fos.toByteArray();
                            return new ByteArrayInputStream(bytes);
                        }
                    }, filename));
                    
                    image.setVisible(true);
                }
            };

            Upload upload = new Upload();
            ImageUploader uploader = new ImageUploader(upload);
            upload.setReceiver(uploader);
            upload.addSucceededListener(uploader);
            upload.setImmediate(true); // Only button

            HorizontalButtonGroup group = new HorizontalButtonGroup();
            group.addComponent(upload);
            layout.addComponent(group);
            
            setContent(layout);
        }
    }

    class ThemeingView extends NavigationView {
        private static final long serialVersionUID = -310203104553728231L;

        public ThemeingView() {
            super("Themeing");
        }

        protected void onBecomingVisible() {
            super.onBecomingVisible();
            
            CssLayout layout = new CssLayout();
            
            Label label = new Label("Check this style!");
            label.addStyleName("stylishlabel");
            layout.addComponent(label);
            
            setContent(layout);
        }
    }    


    class DownloadView extends NavigationView {
        private static final long serialVersionUID = -310203104553728231L;

        public DownloadView() {
            super("Download");
        }

        protected void onBecomingVisible() {
            super.onBecomingVisible();

            VerticalComponentGroup vertical =
                    new VerticalComponentGroup();
            Link link = new Link("Click here to download PDF",
                new ThemeResource("../book-examples/pdfexample.pdf"));
            vertical.addComponent(link);

            setContent(vertical);
        }
    }    
 
    class SlideShow extends NavigationManager
          implements NavigationListener {
        private static final long serialVersionUID = -707028494174423916L;

        String imageNames[] = {"Mercury.jpg", "Venus.jpg",
            "Earth.jpg", "Mars.jpg", "Jupiter.jpg",
            "Saturn.jpg", "Uranus.jpg", "Neptune.jpg"};
        int pos = 0;

        public SlideShow() {
            addStyleName("slideshow");
            
            // Set up the initial views
            navigateTo(createView(pos));
            setNextComponent(createView(pos+1));
            
            addNavigationListener(this);
        }

        // Update the next or previous view after a swipe
        @Override
        public void navigate(NavigationEvent event) {
            switch (event.getDirection()) {
                case FORWARD:
                    if (++pos < imageNames.length-1)
                        setNextComponent(createView(pos+1));
                    break;
                case BACK:
                    if (--pos > 0)
                        setPreviousComponent(createView(pos-1));
            }
        }

        /* Create a view by its position index */
        SwipeView createView(int pos) {
            SwipeView view = new SwipeView();
            view.setSizeFull();
            
            // Use an inner layout to center the image
            VerticalLayout layout = new VerticalLayout();
            layout.setSizeFull();

            Image image = new Image(null, new ThemeResource(
                "../book-examples/img/planets/" + imageNames[pos]));
            layout.addComponent(image);
            layout.setComponentAlignment(image,
                Alignment.MIDDLE_CENTER);

            view.setContent(layout);
            return view;
        }
    }

    class SlideShow2 extends SlideShow {
        private static final long serialVersionUID = -707028635734423916L;

        /* Create a view by its position index */
        @Override
        SwipeView createView(int pos) {
            SwipeView swipe = new SwipeView();
            swipe.setSizeFull();

            // Use an inner layout to center the image
            VerticalLayout layout = new VerticalLayout();
            layout.setSizeFull();

            Image image = new Image(null, new ThemeResource(
                "../book-examples/img/planets/" + imageNames[pos]));
            layout.addComponent(image);
            layout.setComponentAlignment(image,
                Alignment.MIDDLE_CENTER);

            NavigationView view = new NavigationView(layout);
            view.getNavigationBar().setCaption(imageNames[pos]);
            swipe.setContent(view);
            return swipe;
        }
    }
   
    @Override
    protected void init(VaadinRequest request) {
        TabBarView tabBarView = new TabBarView();

        final NavigationManager manager =
                new NavigationManager(new MainView());
        manager.addNavigationListener(new NavigationListener() {
            private static final long serialVersionUID = -7331692292134082502L;

            @Override
            public void navigate(NavigationEvent event) {
                if (event.getDirection() ==
                        NavigationEvent.Direction.BACK) {
                    // Do something
                    Notification.show("You came back to " +
                        manager.getCurrentComponent().getCaption());
                }
            }
        });

        tabBarView.addTab(manager, "Main");

        // Swipe view
        tabBarView.addTab(new SlideShow(), "Swipe");
        tabBarView.addTab(new SlideShow2(), "Swipe 2");
        
        setContent(tabBarView);
    }

    public class Planet {
        String name;
        double orbitRadius;
        int    moons;
        
        public Planet(String name, double orbitRadius, int moons) {
            this.name = name;
            this.orbitRadius = orbitRadius;
            this.moons = moons;
        }
        
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public Double getOrbitRadius() {
            return orbitRadius;
        }
        public void setOrbitRadius(Double orbitRadius) {
            this.orbitRadius = orbitRadius;
        }
        public Integer getMoons() {
            return moons;
        }
        public void setMoons(Integer moons) {
            this.moons = moons;
        }
    }
    
    BeanItemContainer<Planet> planetData() {
        BeanItemContainer<Planet> container =
                new BeanItemContainer<Planet>(Planet.class);
        
        Planet planets[] = {
                new Planet("Mercury",  0.3871, 0),
                new Planet("Venus",    0.7233, 0),
                new Planet("Earth",    1.0000, 1),
                new Planet("Mars",     1.5237, 2),
                new Planet("Jupiter",  5.2028, 63),
                new Planet("Saturn",   9.5388, 62),
                new Planet("Uranus",  19.1914, 27),
                new Planet("Neptune", 30.0611, 13)};
        container.addAll(Lists.newArrayList(planets));
        return container;
    }
}
// END-EXAMPLE: mobile.overview.phone
