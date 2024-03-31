package org.example.labjava02;

import javafx.animation.KeyValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.*;
import java.net.URL;
import java.util.*;

public class StudentController implements Initializable {


        @FXML
        private TextField txtNombreCrearArchivo,txtApellidoCrearArchivo,txtEmailCrearArchivo,txtNombreDelAlumno,txtApellidoDelAlumno,txtEmailDelAlumno,txtBootDelAlumno;
        @FXML
        private ComboBox<String> txtBootCrearArchivo;
        @FXML
        private TextArea txtDatosAlumno,txtDatosDelAlumno;
        @FXML
        private ComboBox<Integer> txtIdDel;

        ArrayList<HashMap<String, String>> alumnosInfo = new ArrayList<>();
        private StudentModel studentModel;


    @FXML
    protected void btnCrearClick(ActionEvent event) {

        //Cierro la ventana actual
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.close();

        //Abro la siguiente ventana para ingresar alumnos y poder guardarlos en un archivo nuevo.
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Student-crearArchivo.fxml"));
            Parent root = fxmlLoader.load();

            //Obtener controlador la vista cargada
            StudentController studentController = fxmlLoader.getController();

            //Fijo el modelo en el controlador
            studentController.setStudentModel(StudentModel.getStudentModel());
            Scene scene = new Scene(root, 400, 500);
            Stage newStage = new Stage();
            newStage.setTitle("Ingresar alumnos");
            newStage.setScene(scene);
            newStage.show();
        } catch (IOException e) {
            System.out.println("Error al abrir la ventana "+e.getMessage());
        }


    }
    @FXML
    protected  void completarCamposEliminarAlumno(){
        //coger id seleccionado
        Integer idSeleccionado = txtIdDel.getValue();
        if(idSeleccionado != null){
            //Bucar alumnos con ese id
            HashMap<String,String> alumnoSeleccionado = null;
            for (HashMap<String,String> alumno: alumnosInfo){
                String idAlumno = alumno.get("id");
                int id = Integer.parseInt(idAlumno);
                if(id == idSeleccionado){
                    alumnoSeleccionado = alumno;
                    break;
                }
            }

            //Actualizar campos de la ventana de Eliminar alumno
            if(alumnoSeleccionado!=null){
                txtNombreDelAlumno.setText(alumnoSeleccionado.get("nombre"));
                txtApellidoDelAlumno.setText(alumnoSeleccionado.get("apellidos"));
                txtEmailDelAlumno.setText(alumnoSeleccionado.get("email"));
                txtBootDelAlumno.setText(alumnoSeleccionado.get("bootcamp"));
            }

        }else{
            System.out.println("No has seleccionado ningún alumno");
        }
    }
    @FXML
    protected void btnCargarClick(ActionEvent event) {
        //Buscar archivo
        FileChooser fileChooser = new FileChooser();
        File elegirArchivo = fileChooser.showOpenDialog(new Stage());
        if(elegirArchivo != null){
            try(BufferedReader reader = new BufferedReader(new FileReader(elegirArchivo));
            ) {
                //Vamos linea por linea obteniendo los datos del archivo
                String line;
                while ((line = reader.readLine()) != null) {
                    //crear el HashMap del alumno
                    HashMap<String, String> alumnoInfo = new HashMap<>();

                    String[] datosAlum = line.split(":");

                    for (String datoAlumno : datosAlum) {
                        String[] datos = datoAlumno.split(",");

                        if (datos.length == 2) {
                            String key = datos[0].trim();
                            String value = datos[1].trim();
                            alumnoInfo.put(key, value);

                        } else if (datos.length == 0) {
                            System.out.println("linea vacia entre alumnos");
                        } else {

                            VentanaError("ERROR", "Archivo erroneo", "Formato del archivo incorrecto");
                        }
                    }
                    //Lo agrego al HashMap del al array.
                    alumnosInfo.add(alumnoInfo);
                }

                studentModel.setAlumnos(alumnosInfo);

                //Abro la siguiente ventana para ingresar alumnos y poder guardarlos en un archivo nuevo.
               try {
                    Stage newStage = new Stage();
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Student-crearArchivo.fxml"));
                    Parent root = fxmlLoader.load();
                    Scene scene = new Scene(root, 400, 500);
                    newStage.setTitle("Alumnos");
                    newStage.setScene(scene);
                    newStage.show();

                    StudentController studentController = fxmlLoader.getController();
                    studentController.setStudentModel(studentModel);

                } catch (IOException e) {
                    System.out.println("Error al abrir la ventana "+e.getMessage());
                }

                //Cierro la ventana actual
                Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
                stage.close();

            }catch (IOException e) {
                e.printStackTrace();
                VentanaError("ERROR", "Archivo erroneo", "Archivo incorrecto");
            }
        }else{
            VentanaError("ERROR", "Archivo erroneo", "El archivo no es legible");
        }
    }

    @FXML
    protected void agregarAlumnoArchNuevo(ActionEvent event){

        //obtengo los datos del alumno
        String nombre = txtNombreCrearArchivo.getText();
        String apellido = txtApellidoCrearArchivo.getText();
        String email = txtEmailCrearArchivo.getText();
        String bootcamp = txtBootCrearArchivo.getValue();

        // Verificar si algún campo está vacío
        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || bootcamp == null) {
            VentanaError("ERROR", "Algún campo esta vacio", "Por favor complete todos los campos.");
            return;
        }

        //obtener id disponible
        int idDispobible = getIdDisponible();

        //crear el HashMap del alumno
        HashMap<String,String> AlumnoInfo = new HashMap<>();
        AlumnoInfo.put("id", String.valueOf(idDispobible));
        AlumnoInfo.put("nombre", nombre);
        AlumnoInfo.put("apellidos", apellido);
        AlumnoInfo.put("email", email);
        AlumnoInfo.put("bootcamp", bootcamp);

        alumnosInfo.add(AlumnoInfo);

        //guardar alumnos en modelo
        studentModel.setAlumnos(alumnosInfo);

        mostrarAlumnosEnTextArea(alumnosInfo);
        limpiarDatosCrearArchivo();
    }
    @FXML
    protected void limpiarDatosCrearArchivo() {
        txtNombreCrearArchivo.clear();
        txtApellidoCrearArchivo.clear();
        txtEmailCrearArchivo.clear();
        txtBootCrearArchivo.getSelectionModel().clearSelection();
    }
    @FXML
    protected void limpiarDatosEliminarArchivo() {
        txtNombreDelAlumno.clear();
        txtApellidoDelAlumno.clear();
        txtEmailDelAlumno.clear();
        txtBootDelAlumno.clear();
    }
    @FXML
    protected void Salir(ActionEvent event){
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();

    }
    @FXML
    protected void saveInfoAlumnos() throws IOException {
        //creamos el archivo con la información de los alumnos separado por comas entre clave-valor y punto entre información
        BufferedWriter writer = new BufferedWriter(new FileWriter("listado de alumnos.txt"));

        //obtenemos la información de los alumnos
        ArrayList<HashMap<String,String>> alumnosInformacion = studentModel.getAlumnosInfo();

        //recorrer lista para imprimir en text area
        for(int i=0;i<alumnosInformacion.size();i++){
            //Creamos una variable para concatenar toda la informacion
            StringBuilder infoAlumnos = new StringBuilder();
            infoAlumnos.append("id,").append((alumnosInformacion.get(i).get("id"))).append(":");
            infoAlumnos.append("nombre,").append(alumnosInformacion.get(i).get("nombre").trim()).append(":");
            infoAlumnos.append("apellidos,").append(alumnosInformacion.get(i).get("apellidos").trim()).append(":");
            infoAlumnos.append("email,").append(alumnosInformacion.get(i).get("email").trim()).append(":");
            infoAlumnos.append("bootcamp,").append(alumnosInformacion.get(i).get("bootcamp").trim());
            writer.write(String.valueOf(infoAlumnos));
            writer.newLine();
        }
        writer.close();
    }
    @FXML
    private void windowDeleteAlumno(){
        try {
            Stage newStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Student-delete.fxml"));
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root, 400, 500);
            newStage.setTitle("Alumnos");
            newStage.setScene(scene);


            newStage.setOnShown(event ->{

                StudentController studentController = fxmlLoader.getController();
                studentController.setStudentModel(studentModel);

                List<Integer> idExistentes = obtenerIdExistentes();
                studentController.completarIdBox(idExistentes);

                //mostrarEliminarAlumnosTextArea(alumnosInfo);
            });
            newStage.show();

        }catch (IOException e) {
            System.out.println("Error al abrir la ventana "+e.getMessage());
        }
    }
    private List<Integer> obtenerIdExistentes(){
        //obtener los Id de los alumnos
        List<Integer> idExistentes = new ArrayList<>();
        for (HashMap<String,String> alumno : alumnosInfo){
            String idString = alumno.get("id");
            int id = Integer.parseInt(idString);
            idExistentes.add(id);
        }
        Collections.sort(idExistentes);

        return idExistentes;
    }
    private void completarIdBox(List<Integer> idExistentes){
        ObservableList<Integer> observableList = FXCollections.observableArrayList(idExistentes);
        txtIdDel.setItems(observableList);
    }
    @FXML
    private void eliminarAlumno(){
        //coger id seleccionado
        Integer idSeleccionado = txtIdDel.getValue();

        if(idSeleccionado == null){
            VentanaError("ERROR", "No ha seleccionado ningún alumno", "Por favor, seleccione el alumno que quiere eliminar.");
            return;
        }

        //Bucar alumnos con ese id
        HashMap<String,String> alumnoSeleccionado = null;
        for (HashMap<String,String> alumno: alumnosInfo){
            String idAlumno = alumno.get("id");
            int id = Integer.parseInt(idAlumno);
            if(id == idSeleccionado){
                alumnoSeleccionado = alumno;
                break;
            }
        }
        alumnosInfo.remove(alumnoSeleccionado);

        //Actualizar los campos
        List<Integer> idExistentes = obtenerIdExistentes();
        completarIdBox(idExistentes);
        limpiarDatosEliminarArchivo();
        mostrarEliminarAlumnosTextArea(alumnosInfo);

    }
    @FXML
    private void ActualizarAlumnos(){
        mostrarAlumnosEnTextArea(alumnosInfo);
    }
    @FXML
    private void actualizarEliminarAlumno(){
        mostrarEliminarAlumnosTextArea(alumnosInfo);
    }
    public static void VentanaError(String titulo, String encabezado, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(mensaje);

        alert.showAndWait();
    }
    private void setStudentModel(StudentModel studentModel){
        this.studentModel = studentModel;
        alumnosInfo = studentModel.getAlumnosInfo();

    }
    private void mostrarAlumnosEnTextArea(ArrayList<HashMap<String,String>> alumnosInfo2) {
        StringBuilder infoAlumnos = new StringBuilder();
        for (HashMap<String,String> alumno : alumnosInfo2){
            infoAlumnos.append("ID: ").append(alumno.get("id")).append("\n");
            infoAlumnos.append("Nombre: ").append(alumno.get("nombre")).append("\n");
            infoAlumnos.append("Apellidos: ").append(alumno.get("apellidos")).append("\n");
            infoAlumnos.append("E-mail: ").append(alumno.get("email")).append("\n");
            infoAlumnos.append("Bootcamp: ").append(alumno.get("bootcamp")).append("\n\n");
        }

        txtDatosAlumno.setText(infoAlumnos.toString());

    }
    private void mostrarEliminarAlumnosTextArea(ArrayList<HashMap<String,String>> alumnosInfo3) {
        StringBuilder infoAlumnos = new StringBuilder();
        for (HashMap<String,String> alumno : alumnosInfo3){
            infoAlumnos.append("ID: ").append(alumno.get("id")).append("\n");
            infoAlumnos.append("Nombre: ").append(alumno.get("nombre")).append("\n");
            infoAlumnos.append("Apellidos: ").append(alumno.get("apellidos")).append("\n");
            infoAlumnos.append("E-mail: ").append(alumno.get("email")).append("\n");
            infoAlumnos.append("Bootcamp: ").append(alumno.get("bootcamp")).append("\n\n");
        }

        txtDatosDelAlumno.setText(infoAlumnos.toString());

    }
    private int getIdDisponible(){
        //creamos un conjunto para los id existentes
        Set<Integer> idExistentes = new HashSet<>();

        //Obteneermos los id que existen
        for(HashMap<String,String> idAlumnos :alumnosInfo){
            String idString = idAlumnos.get("id");
            int id = Integer.parseInt(idString);
            idExistentes.add(id);
        }

        //Encontrar id disponible
        int nextId = 1;
        while(idExistentes.contains(nextId)){
            nextId++;
        }
        return nextId;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        studentModel = StudentModel.getStudentModel();
    }
}
