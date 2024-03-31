package org.example.labjava02;

import java.util.ArrayList;
import java.util.HashMap;

public class StudentModel {

    private ArrayList<HashMap<String,String>> alumnoInfo;
    private static  StudentModel studentModel;
    private StudentModel() {
        this.alumnoInfo = new ArrayList<>();
    }
    public static StudentModel getStudentModel() {
        if (studentModel == null) {
            studentModel = new StudentModel();
        }
        return studentModel;
    }
    public void setStudentModel(StudentModel studentModel){
        StudentModel.studentModel = studentModel;
    }
    public ArrayList<HashMap<String,String>> getAlumnosInfo() {
        return alumnoInfo;
    }

    public void setAlumnoInfo(HashMap<String, String> addAlumno) {
        alumnoInfo.add(addAlumno);
    }

    public void setAlumnos (ArrayList<HashMap<String,String>> alumnos){
        alumnoInfo = alumnos;
    }
    public void mostrarAlumnos(){
        System.out.println(alumnoInfo);
    }
}
