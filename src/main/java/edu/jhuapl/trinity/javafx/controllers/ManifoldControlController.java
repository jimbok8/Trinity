package edu.jhuapl.trinity.javafx.controllers;

/*-
 * #%L
 * trinity
 * %%
 * Copyright (C) 2021 - 2023 The Johns Hopkins University Applied Physics Laboratory LLC
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import edu.jhuapl.trinity.App;
import edu.jhuapl.trinity.data.Distance;
import edu.jhuapl.trinity.data.FactorLabel;
import edu.jhuapl.trinity.data.Manifold;
import edu.jhuapl.trinity.javafx.components.DistanceListItem;
import edu.jhuapl.trinity.javafx.components.ManifoldListItem;
import edu.jhuapl.trinity.javafx.events.CommandTerminalEvent;
import edu.jhuapl.trinity.javafx.events.ManifoldEvent;
import edu.jhuapl.trinity.javafx.javafx3d.Manifold3D;
import edu.jhuapl.trinity.utils.ResourceUtils;
import edu.jhuapl.trinity.utils.umap.Umap;
import edu.jhuapl.trinity.utils.umap.metric.Metric;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.paint.PhongMaterial;

/**
 * FXML Controller class
 *
 * @author Sean Phillips
 */
public class ManifoldControlController implements Initializable {
    //Geometry Tab
    @FXML
    private RadioButton useVisibleRadioButton;
    @FXML
    private RadioButton useAllRadioButton;
    ToggleGroup pointsToggleGroup;
    @FXML
    private ChoiceBox labelChoiceBox;
    @FXML
    private CheckBox automaticCheckBox;
    @FXML
    private Spinner manualSpinner;
    @FXML
    private ColorPicker manifoldDiffuseColorPicker;
    @FXML
    private ColorPicker manifoldSpecularColorPicker;
    @FXML
    private ColorPicker manifoldWireMeshColorPicker;
    @FXML
    private RadioButton frontCullFaceRadioButton;
    @FXML
    private RadioButton backCullFaceRadioButton;
    @FXML
    private RadioButton noneCullFaceRadioButton;
    ToggleGroup cullfaceToggleGroup;
    @FXML
    private RadioButton fillDrawModeRadioButton;
    @FXML
    private RadioButton linesDrawModeRadioButton;
    ToggleGroup drawModeToggleGroup;
    @FXML
    private CheckBox showWireframeCheckBox;
    @FXML
    private CheckBox showControlPointsCheckBox;

    //UMAP tab
    @FXML
    private Slider repulsionSlider;
    @FXML
    private Slider minDistanceSlider;
    @FXML
    private Slider spreadSlider;
    @FXML
    private Slider opMixSlider;
    @FXML
    private Spinner numComponentsSpinner;
    @FXML
    private Spinner numEpochsSpinner;
    @FXML
    private Spinner nearestNeighborsSpinner;
    @FXML
    private Spinner negativeSampleRateSpinner;
    @FXML
    private Spinner localConnectivitySpinner;
    @FXML
    private ChoiceBox metricChoiceBox;
    @FXML
    private CheckBox verboseCheckBox;
    //Geometry Tab
    @FXML
    private RadioButton useHyperspaceButton;
    @FXML
    private RadioButton useHypersurfaceButton;
    ToggleGroup hyperSourceGroup;
    @FXML
    private ListView<ManifoldListItem> manifoldsListView;
    private Manifold3D activeManifold3D = null;
    
    //Distances Tab
    @FXML
    private ListView<DistanceListItem> distancesListView;
    @FXML
    private RadioButton pointToPointRadioButton;
    @FXML
    private RadioButton pointToGroupRadioButton;
    ToggleGroup pointModeToggleGroup;
    @FXML
    private TextField distanceMetricTextField;
    @FXML
    private ColorPicker connectorColorPicker;
    @FXML
    private Spinner connectorThicknessSpinner;

    Scene scene;
    private final String ALL = "ALL";
    boolean reactive = true;
    
    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        scene = App.getAppScene();
        setupHullControls();
        setupUmapControls();
        setupDistanceControls();
    }

    private void setupDistanceControls() {
        distanceMetricTextField.setText("Select Distance Object");
        distanceMetricTextField.setEditable(false);
        connectorThicknessSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 5, 1));
        connectorThicknessSpinner.setEditable(true);
        connectorThicknessSpinner.valueProperty().addListener(e -> {
            DistanceListItem item = distancesListView.getSelectionModel().getSelectedItem();
            if (null != item) {
                Integer width = (Integer) connectorThicknessSpinner.getValue();
                item.getDistance().setWidth(width);
                scene.getRoot().fireEvent(
                    new ManifoldEvent(ManifoldEvent.DISTANCE_CONNECTOR_WIDTH, item.getDistance()));
            }
        });
        connectorThicknessSpinner.setInitialDelay(Duration.millis(500));
        connectorThicknessSpinner.setRepeatDelay(Duration.millis(500));

        connectorColorPicker.valueProperty().addListener(cl -> {
            DistanceListItem item = distancesListView.getSelectionModel().getSelectedItem();
            if (null != item) {
                item.getDistance().setColor(connectorColorPicker.getValue());
                scene.getRoot().fireEvent(
                    new ManifoldEvent(ManifoldEvent.DISTANCE_CONNECTOR_COLOR, item.getDistance()));
            }
        });
        //Get a reference to any Distances already collected
        List<DistanceListItem> existingItems = new ArrayList<>();
        for (Distance d : Distance.getDistances()) {
            DistanceListItem item = new DistanceListItem(d);
            existingItems.add(item);
        }
        //add them all in one shot
        distancesListView.getItems().addAll(existingItems);
        ImageView iv = ResourceUtils.loadIcon("metric", 200);
        VBox placeholder = new VBox(10, iv, new Label("No Distances Acquired"));
        placeholder.setAlignment(Pos.CENTER);
        distancesListView.setPlaceholder(placeholder);

        //Bind disable properties so that controls only active when item is selected
        distanceMetricTextField.disableProperty().bind(
            distancesListView.getSelectionModel().selectedIndexProperty().lessThan(0));
        connectorThicknessSpinner.disableProperty().bind(
            distancesListView.getSelectionModel().selectedIndexProperty().lessThan(0));
        connectorColorPicker.disableProperty().bind(
            distancesListView.getSelectionModel().selectedIndexProperty().lessThan(0));

        pointModeToggleGroup = new ToggleGroup();
        pointToPointRadioButton.setToggleGroup(pointModeToggleGroup);
        pointToGroupRadioButton.setToggleGroup(pointModeToggleGroup);
        scene.addEventHandler(ManifoldEvent.DISTANCE_CONNECTOR_SELECTED, e -> {
            Distance distance = (Distance) e.object1;
            for (DistanceListItem item : distancesListView.getItems()) {
                if (item.getDistance() == distance) {
                    distancesListView.getSelectionModel().select(item);
                    distanceMetricTextField.setText(distance.getMetric());
                    connectorColorPicker.setValue(distance.getColor());
                    connectorThicknessSpinner.getValueFactory().setValue(distance.getWidth());
                    return; //break out early
                }
            }
            //if we get here its because that Distance object wasn't in the list
            scene.getRoot().fireEvent(new CommandTerminalEvent(
                "Distance object not found!", new Font("Consolas", 20), Color.YELLOW));
        });
        scene.addEventHandler(ManifoldEvent.DISTANCE_OBJECT_SELECTED, e -> {
            Distance distance = (Distance) e.object1;
            distanceMetricTextField.setText(distance.getMetric());
            connectorColorPicker.setValue(distance.getColor());
            connectorThicknessSpinner.getValueFactory().setValue(distance.getWidth());
        });
        scene.addEventHandler(ManifoldEvent.CREATE_NEW_DISTANCE, e -> {
            Distance distance = (Distance) e.object1;
            DistanceListItem distanceListItem = new DistanceListItem(distance);
            Distance.addDistance(distance);
            distancesListView.getItems().add(distanceListItem);
        });
    }

    private void getCurrentLabels() {
        labelChoiceBox.getItems().clear();
        labelChoiceBox.getItems().add(ALL);
        labelChoiceBox.getItems().addAll(
            FactorLabel.getFactorLabels().stream().map(f -> f.getLabel()).toList());
    }

    private void setupUmapControls() {
        metricChoiceBox.getItems().addAll(Metric.getMetricNames());
        metricChoiceBox.getSelectionModel().selectFirst();

        numComponentsSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 100, 3, 1));
        numEpochsSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(25, 500, 200, 25));
        nearestNeighborsSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 100, 15, 5));
        negativeSampleRateSpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 5, 1));
        localConnectivitySpinner.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1, 1));

        hyperSourceGroup = new ToggleGroup();
        useHyperspaceButton.setToggleGroup(hyperSourceGroup);
        useHypersurfaceButton.setToggleGroup(hyperSourceGroup);
    }

    private void setupHullControls() {
        //Get a reference to any Distances already collected
        List<ManifoldListItem> existingItems = new ArrayList<>();
        for (Manifold m : Manifold.getManifolds()) {
            ManifoldListItem item = new ManifoldListItem(m);
            existingItems.add(item);
        }
        //add them all in one shot
        manifoldsListView.getItems().addAll(existingItems);
        ImageView iv = ResourceUtils.loadIcon("manifold", 200);
        VBox placeholder = new VBox(10, iv, new Label("No Manifolds Acquired"));
        placeholder.setAlignment(Pos.CENTER);
        manifoldsListView.setPlaceholder(placeholder);
        
        getCurrentLabels();
        labelChoiceBox.getSelectionModel().selectFirst();
        labelChoiceBox.setOnShown(e -> getCurrentLabels());
        manualSpinner.setValueFactory(
            new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 1, 0.5, 0.1));
        manualSpinner.setEditable(true);
        //whenever the spinner value is changed...
        manualSpinner.valueProperty().addListener(e -> {
            scene.getRoot().fireEvent(
                new ManifoldEvent(ManifoldEvent.SET_DISTANCE_TOLERANCE,
                    (Double) manualSpinner.getValue()));
        });
        manualSpinner.disableProperty().bind(automaticCheckBox.selectedProperty());

        manifoldDiffuseColorPicker.setValue(Color.CYAN);
        manifoldDiffuseColorPicker.valueProperty().addListener(cl -> {
            if(!reactive) return;
            ManifoldListItem item = manifoldsListView.getSelectionModel().getSelectedItem();
            if(null != item) {
                Manifold m = item.getManifold();
                if(null != m){
                    m.setColor(manifoldDiffuseColorPicker.getValue());
                    scene.getRoot().fireEvent(new ManifoldEvent(
                        ManifoldEvent.MANIFOLD_DIFFUSE_COLOR,
                        manifoldDiffuseColorPicker.getValue(), m));
                }
            }
        });
        manifoldSpecularColorPicker.setValue(Color.RED);
        manifoldSpecularColorPicker.valueProperty().addListener(cl -> {
            if(!reactive) return;
            ManifoldListItem item = manifoldsListView.getSelectionModel().getSelectedItem();
            if(null != item) {
                Manifold m = item.getManifold();
                if(null != m)
                    scene.getRoot().fireEvent(new ManifoldEvent(
                    ManifoldEvent.MANIFOLD_SPECULAR_COLOR,
                    manifoldSpecularColorPicker.getValue(), m));
            }
        });
        manifoldWireMeshColorPicker.setValue(Color.BLUE);
        manifoldWireMeshColorPicker.valueProperty().addListener(cl -> {
            if(!reactive) return;
            ManifoldListItem item = manifoldsListView.getSelectionModel().getSelectedItem();
            if(null != item) {
                Manifold m = item.getManifold();
                if(null != m)
                    scene.getRoot().fireEvent(new ManifoldEvent(
                ManifoldEvent.MANIFOLD_WIREFRAME_COLOR,
                manifoldWireMeshColorPicker.getValue(),m));
            }
        });

        pointsToggleGroup = new ToggleGroup();
        useVisibleRadioButton.setToggleGroup(pointsToggleGroup);
        useAllRadioButton.setToggleGroup(pointsToggleGroup);
        pointsToggleGroup.selectedToggleProperty().addListener(cl -> {
            if (useVisibleRadioButton.isSelected())
                scene.getRoot().fireEvent(new ManifoldEvent(
                    ManifoldEvent.USE_VISIBLE_POINTS, true));
            else
                scene.getRoot().fireEvent(new ManifoldEvent(
                    ManifoldEvent.USE_ALL_POINTS, true));
        });

        cullfaceToggleGroup = new ToggleGroup();
        frontCullFaceRadioButton.setToggleGroup(cullfaceToggleGroup);
        backCullFaceRadioButton.setToggleGroup(cullfaceToggleGroup);
        noneCullFaceRadioButton.setToggleGroup(cullfaceToggleGroup);
        cullfaceToggleGroup.selectedToggleProperty().addListener(cl -> {
            Manifold m = manifoldsListView.getSelectionModel().getSelectedItem().getManifold();
            if(null != m)
                if (frontCullFaceRadioButton.isSelected())
                    scene.getRoot().fireEvent(new ManifoldEvent(
                        ManifoldEvent.MANIFOLD_FRONT_CULLFACE, true, m));
                else if (backCullFaceRadioButton.isSelected())
                    scene.getRoot().fireEvent(new ManifoldEvent(
                        ManifoldEvent.MANIFOLD_BACK_CULLFACE, true, m));
                else
                    scene.getRoot().fireEvent(new ManifoldEvent(
                        ManifoldEvent.MANIFOLD_NONE_CULLFACE, true, m));
        });
        drawModeToggleGroup = new ToggleGroup();
        fillDrawModeRadioButton.setToggleGroup(drawModeToggleGroup);
        linesDrawModeRadioButton.setToggleGroup(drawModeToggleGroup);
        drawModeToggleGroup.selectedToggleProperty().addListener(cl -> {
            Manifold m = manifoldsListView.getSelectionModel().getSelectedItem().getManifold();
            if(null != m)
                if (fillDrawModeRadioButton.isSelected())
                    scene.getRoot().fireEvent(new ManifoldEvent(
                        ManifoldEvent.MANIFOLD_FILL_DRAWMODE, true, m));
                else
                    scene.getRoot().fireEvent(new ManifoldEvent(
                        ManifoldEvent.MANIFOLD_LINE_DRAWMODE, true, m));
        });

        showWireframeCheckBox.selectedProperty().addListener(cl -> {
            Manifold m = manifoldsListView.getSelectionModel().getSelectedItem().getManifold();
            if(null != m)
                scene.getRoot().fireEvent(new ManifoldEvent(
                ManifoldEvent.MANIFOLD_SHOW_WIREFRAME, 
                    showWireframeCheckBox.isSelected(), m));
        });
        showControlPointsCheckBox.selectedProperty().addListener(cl -> {
            Manifold m = manifoldsListView.getSelectionModel().getSelectedItem().getManifold();
            if(null != m)
                scene.getRoot().fireEvent(new ManifoldEvent(
                ManifoldEvent.MANIFOLD_SHOW_CONTROL, 
                    showControlPointsCheckBox.isSelected(), m));
        });
        
        scene.addEventHandler(ManifoldEvent.MANIFOLD_3D_SELECTED, e -> {
            Manifold manifold = (Manifold) e.object1;
            for (ManifoldListItem item : manifoldsListView.getItems()) {
                if (item.getManifold() == manifold) {
                    manifoldsListView.getSelectionModel().select(item);
                    Manifold3D manifold3D = Manifold.globalManifoldToManifold3DMap.get(manifold);
                    updateActiveManifold3D(manifold3D);
                    return; //break out early
                }
            }
            //if we get here its because that Distance object wasn't in the list
            scene.getRoot().fireEvent(new CommandTerminalEvent(
                "Manifold object not found!", new Font("Consolas", 20), Color.YELLOW));
        });
        scene.addEventHandler(ManifoldEvent.MANIFOLD_OBJECT_SELECTED, e -> {
            Manifold manifold = (Manifold) e.object1;
            Manifold3D manifold3D = Manifold.globalManifoldToManifold3DMap.get(manifold);
            updateActiveManifold3D(manifold3D);
        });
        scene.addEventHandler(ManifoldEvent.MANIFOLD3D_OBJECT_GENERATED, e -> {
            Manifold manifold = (Manifold) e.object1;
            updateActiveManifold3D((Manifold3D) e.object2);
            ManifoldListItem manifoldListItem = new ManifoldListItem(manifold);
            manifoldsListView.getItems().add(manifoldListItem);
            manifoldsListView.getSelectionModel().selectLast();
        });
    }
    private void updateActiveManifold3D(Manifold3D manifold3D) {
        reactive = false;
        activeManifold3D = manifold3D;
        if(null != manifold3D) {
            PhongMaterial phong = (PhongMaterial)manifold3D.quickhullMeshView.getMaterial();
            manifoldDiffuseColorPicker.setValue(phong.getDiffuseColor());
            manifoldSpecularColorPicker.setValue(phong.getSpecularColor());
            manifoldWireMeshColorPicker.setValue(((PhongMaterial)
                manifold3D.quickhullLinesMeshView.getMaterial()).getDiffuseColor());
        }
        reactive = true;
    }

    @FXML
    public void project() {
        Umap umap = new Umap();
        umap.setRepulsionStrength((float) repulsionSlider.getValue());
        umap.setMinDist((float) minDistanceSlider.getValue());
        umap.setSpread((float) spreadSlider.getValue());
        umap.setSetOpMixRatio((float) opMixSlider.getValue());
        umap.setNumberComponents((int) numComponentsSpinner.getValue());
        umap.setNumberEpochs((int) numEpochsSpinner.getValue());
        umap.setNumberNearestNeighbours((int) nearestNeighborsSpinner.getValue());
        umap.setNegativeSampleRate((int) negativeSampleRateSpinner.getValue());
        umap.setLocalConnectivity((int) localConnectivitySpinner.getValue());
        umap.setMetric((String) metricChoiceBox.getValue());
        umap.setVerbose(verboseCheckBox.isSelected());
        ManifoldEvent.POINT_SOURCE pointSource = useHypersurfaceButton.isSelected() ?
            ManifoldEvent.POINT_SOURCE.HYPERSURFACE : ManifoldEvent.POINT_SOURCE.HYPERSPACE;
        scene.getRoot().fireEvent(new ManifoldEvent(
            ManifoldEvent.GENERATE_NEW_UMAP, umap, pointSource));
    }

    @FXML
    public void generate() {
        scene.getRoot().fireEvent(new ManifoldEvent(
            ManifoldEvent.GENERATE_PROJECTION_MANIFOLD, useVisibleRadioButton.isSelected(), (String) labelChoiceBox.getValue()));
    }

    @FXML
    public void clearAll() {
        scene.getRoot().fireEvent(new ManifoldEvent(
            ManifoldEvent.CLEAR_ALL_MANIFOLDS));
        //add them all in one shot
        manifoldsListView.getItems().clear();
    }

    @FXML
    public void startConnector() {
        //fire event to put projection view into connector mode
        //@TODO SMP Hardcoded for now to Euclidean
        if (pointToGroupRadioButton.isSelected())
            scene.getRoot().fireEvent(new ManifoldEvent(
                ManifoldEvent.DISTANCE_MODE_POINTGROUP, pointToGroupRadioButton.isSelected(), "euclidean"));
        else
            scene.getRoot().fireEvent(new ManifoldEvent(
                ManifoldEvent.DISTANCE_MODE_POINTPOINT, pointToPointRadioButton.isSelected(), "euclidean"));
    }

    @FXML
    public void clearAllDistances() {
        distancesListView.getItems().clear();
        Distance.removeAllDistances(); //will fire event notifying scene
        scene.getRoot().fireEvent(
            new ManifoldEvent(ManifoldEvent.CLEAR_DISTANCE_CONNECTORS));
    }
}
