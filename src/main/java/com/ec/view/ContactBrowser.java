package com.ec.view;

import com.ec.model.ContactDAO;
import com.ec.model.ContactPerson;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ContactBrowser extends Application {
    private String[] propertyName = {"id", "name", "nickName", "address", "homePhone", "workPhone", "cellPhone", "email", "birthDate", "webSite", "profession"};
    private String[] propertyLabel = {"ID", "Name", "Nick Name", "Address", "Home Phone", "WorkPhone", "Cell Phone", "Email", "Birth Date", "WebSite", "Profession"};

    private ContactDAO contact = new ContactDAO();
    private final GridPane gridPane = new GridPane();
    private final Label lblName = new Label("Search by Name");
    private final TextField searchField = new TextField();
    private final ListView<ContactPerson> listView = new ListView<>();
    TableView<ContactPerson> contactTableView = new TableView<>();
    private ObservableList<ContactPerson> observableNames;
    private FilteredList<ContactPerson> filteredData;
    private SortedList<ContactPerson> sortedData;
    private VBox vBox = new VBox();
    private Button btnRefresh = new Button("Refresh");


    public ContactBrowser() {
        lblName.setTextFill(Color.web("#0076a3"));
        observableNames = FXCollections.observableArrayList(contact.getContacts());
        filteredData = new FilteredList<>(observableNames, p -> true);
        sortedData = new SortedList<>(filteredData);
        this.listView.setItems(sortedData);
    }

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Address Book");
        primaryStage.setMaximized(true);

        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane, 650, 400, true);

        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.add(lblName, 0, 0);
        gridPane.add(searchField, 0, 1);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(str -> {
                if (newValue == null || newValue.isEmpty())
                    return true;

                if (str.getName().toLowerCase().contains(newValue.toLowerCase()))
                    return true;

                return false;
            });
        });



        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setPrefHeight(Integer.MAX_VALUE);
        listView.setPrefWidth(100.0);

        listView.setCellFactory(listView -> new ListCell<ContactPerson>(){
            @Override
            protected void updateItem(ContactPerson contactPerson, boolean empty) {
                super.updateItem(contactPerson, empty);
                if (contactPerson != null) {
                    setText(contactPerson.getName());
                } else {
                    setText(null);
                }
            }
        });

        gridPane.add(listView, 0, 2);

        ObservableList<ContactPerson> contactPeopleList = FXCollections.observableArrayList();

        contactTableView.setItems(contactPeopleList);
        contactTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        for (int i = 0; i < propertyLabel.length; i++){
            TableColumn<ContactPerson, Object> col = new TableColumn<ContactPerson, Object>(propertyLabel[i]);
            col.setCellValueFactory(new PropertyValueFactory<ContactPerson, Object>(propertyName[i]));
            contactTableView.getColumns().add(col);
        }

        TableColumn<ContactPerson, Object> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<ContactPerson, Object>, TableCell<ContactPerson, Object>> cellFactory = new Callback<TableColumn<ContactPerson, Object>, TableCell<ContactPerson, Object>>() {
            @Override
            public TableCell<ContactPerson, Object> call(TableColumn<ContactPerson, Object> param) {
                final TableCell<ContactPerson, Object> cell = new TableCell<ContactPerson, Object>(){
                    final Button btn = new Button("Click to follow");
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty){
                            setGraphic(null);
                            setText(null);

                        } else {
                            btn.setOnAction(event -> {
                                ContactPerson contactPerson = getTableView().getItems().get(getIndex());
                                System.out.println(contactPerson.getName());
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
            return cell;


            }
        };

        actionCol.setCellFactory(cellFactory);
        contactTableView.getColumns().add(actionCol);

        borderPane.setCenter(contactTableView);
        borderPane.setLeft(gridPane);

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (observable != null && observable.getValue() != null){
                contactPeopleList.clear();
                contactPeopleList.addAll(contact.getContactsForName(newValue.getName()));
            }
        });

        btnRefresh.setOnAction(event -> {
            System.out.println("Refresh button clicked!");
            observableNames.clear();
            observableNames.addAll(contact.getContacts());
        });

        vBox.getChildren().add(btnRefresh);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(vBox);
        AnchorPane.setBottomAnchor(vBox, 3.0);


        borderPane.setRight(anchorPane);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
