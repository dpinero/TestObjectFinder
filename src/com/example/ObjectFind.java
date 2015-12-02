package com.example;

/**
 * Created by Daniel P on 10/6/2015.
 */

import org.opencv.core.*;
import org.opencv.highgui.Highgui; //will need to change to "import org.opencv.imgcodecs.Imgcodecs;" when opencv 3.0.0 adds moments
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import java.util.ArrayList;
import java.util.List;

public class ObjectFind {
    public static void main(String args[])
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("Hello, OpenCV!");
        new DetectObjectDemo().run();
    }
}

class DetectObjectDemo {
    public void run() {
        System.out.println("\nRunning DetectObjectDemo");
        Mat imagehsv= new Mat();
        Mat imagebinary= new Mat();
        Mat imagebinarytemp= new Mat();
        Mat imagebinary0= new Mat();
        Mat imagebinary1= new Mat();
        Mat imagebinary2= new Mat();
        Mat imagebinary3= new Mat();
        Mat hierarchy = new Mat();
        int threshset = 100;
        //green
        int iLowH0 = 72;
        int iLowS0 = 0;
        int iLowV0 = 0;
        int iHighH0 = 75;
        int iHighS0 = 255;
        int iHighV0 = 165;
        //yellow
        int iLowH1 = 22;
        int iLowS1 = 0;
        int iLowV1 = 0;
        int iHighH1 = 38;
        int iHighS1 = 255;
        int iHighV1 = 255;
        //orange
        int iLowH2 = 0;
        int iLowS2 = 0;
        int iLowV2 = 0;
        int iHighH2 = 19;
        int iHighS2 = 255;
        int iHighV2 = 255;
        //red
        int iLowH3 = 160;
        int iLowS3 = 0;
        int iLowV3 = 0;
        int iHighH3 = 179;
        int iHighS3 = 255;
        int iHighV3 = 255;
        //import selected image
        Mat image = Highgui.imread("C:/Users/Daniel P/Pictures/sample.png"); //will need to change "Highgui" to "Imgcodecs" when opencv 3.0.0 adds moments
        //convert selected image into greyscale
        Imgproc.cvtColor(image, imagehsv, Imgproc.COLOR_BGR2HSV);

        //Imgproc.threshold(imagehsv, imagebinary, threshset, 255, 0);
        //convert image into binary for use in contour function
        //each inRange method forms a binary mat for the color specified above
        Core.inRange(imagehsv, new Scalar(iLowH0, iLowS0, iLowV0) , new Scalar(iHighH0, iHighS0, iHighV0), imagebinary0);
        Core.inRange(imagehsv, new Scalar(iLowH1, iLowS1, iLowV1) , new Scalar(iHighH1, iHighS1, iHighV1), imagebinary1);
        Core.inRange(imagehsv, new Scalar(iLowH2, iLowS2, iLowV2) , new Scalar(iHighH2, iHighS2, iHighV2), imagebinary2);
        Core.inRange(imagehsv, new Scalar(iLowH3, iLowS3, iLowV3) , new Scalar(iHighH3, iHighS3, iHighV3), imagebinary3);
        // the next two-three lines bitwise or combine the maps to delete inside black objects
        Core.bitwise_or(imagebinary0, imagebinary1, imagebinarytemp);
        Core.bitwise_or(imagebinarytemp, imagebinary2, imagebinarytemp);
        Core.bitwise_or(imagebinarytemp, imagebinary3 ,imagebinary);
        Mat imagebinary4 = imagebinary;
        // Save the binary image
        String filename = "C:/Users/Daniel P/Pictures/samplebinary.png";
        System.out.println(String.format("Writing %s", filename));
        Highgui.imwrite(filename, imagebinary); //will need to change "Highgui" to "Imgcodecs" when opencv 3.0.0 adds moments
        // find objects
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(imagebinary, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
        if (contours.size() > 0) {
            int numObjects = contours.size();
            System.out.println(String.format("Detected %s contours", numObjects));
        }
            else{
            System.out.println("No contours detected");
        }
        double[] area=new double[contours.size()];
        double[] posX=new double[contours.size()];
        double[] posY=new double[contours.size()];
        for (int i=0; i< contours.size(); i++) {
            Moments moment = Imgproc.moments(contours.get(i));
            // find object moments
            double dM01 = moment.get_m01();
            double dM10 = moment.get_m10();
            double areaint = moment.get_m00();
            // find object position data
            double posXs = dM10/areaint;
            double posYs = dM01/areaint;
            // place object data into appropriate array
            posX[i] = posXs;
            posY[i] = posYs;
            area[i]=areaint;
            Core.circle(imagebinary4, new Point(posXs, posYs), 4, new Scalar(255, 255, 0));
            // print out all detected object areas
            System.out.println(String.format("The %s object has an x position of %s and a y position of %s",i, posXs,posYs));
            System.out.println(String.format("The %s object has an area of %s", i,areaint));

        }
        // write binary image with markers to new image file
        String filename2 = "C:/Users/Daniel P/Pictures/samplewithmarkers.png";
        System.out.println(String.format("Writing %s", filename2));
        Highgui.imwrite(filename2, imagebinary4); //will need to change "Highgui" to "Imgcodecs" when opencv 3.0.0 adds moments
    }
}