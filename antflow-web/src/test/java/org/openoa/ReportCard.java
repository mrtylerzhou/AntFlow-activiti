package org.openoa;
import java.util.*;

//public class ReportCard {
//
//    public String studentName;
//    public ArrayList clines;
//
//    public void printReport() {
//        System.out.println("Report card for " + studentName);
//        System.out.println("------------------------------");
//        System.out.println("Course Title              Grade");
//        Iterator  grades = clines.Iterator();
//        CourseGrade grade;
//        double avg=0.0;
//        while(grades.hasNext()){
//            grade =(CourseGrade)grades.next();
//            System.out.println(grade.title +"   " + grade.grade);
//            if(!(grade.grade=='F')){
//                avg=avg + grade.grade -64;
//            }
//        }
//        avg=avg/clines.size();
//        System.out.println("--------------------------------");
//        System.out.println("Grade Point Average =" + avg);
//    }
//
//    public class CourseGrade{
//        public String title;
//        public String grade;
//    }
//}



//
//
//
//public class ReportCard {
//    public String studentName;
//    public ArrayList clines;
//
//    public void printReport() {
//        printReportHeader();
//        printReportlines();
//        double avg = calculateAverage();
//        printReportFooter(avg);
//    }
//
//    private void printReportHeader(){
//        System.out.println("Report card for " + studentName);
//        System.out.println("------------------------------");
//        System.out.println("Course Title              Grade");
//    }
//
//    private void printReportFooter(double avg) {
//        System.out.println("--------------------------------");
//        System.out.println("Grade Point Average =" + avg);
//    }
//
//    private void printReportlines() {
//        Iterator  grades = clines.Iterator();
//        while(grades.hasNext()){
//            grade =(CourseGrade)grades.next();
//            System.out.println(grade.title +"   " + grade.grade);
//        }
//    }
//
//    private double calculateAverage() {
//        Iterator  grades = clines.Iterator();
//        double avg=0.0;
//        while(grades.hasNext()){
//            grade =(CourseGrade)grades.next();
//            if(!(grade.grade=='F')){
//                avg=avg + grade.grade -64;
//            }
//        }
//        avg=avg/clines.size();
//        return  avg;
//    }
//
//    public class CourseGrade{
//        public String title;
//        public String grade;
//    }
//}
//
