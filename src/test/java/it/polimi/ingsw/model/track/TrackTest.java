package it.polimi.ingsw.model.track;

import it.polimi.ingsw.utility.GSON;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TrackTest {

   File trackConfigFile = new File("src/SquareConfig.json");
   Track track;
   Track track2;

   @Test
   void calculateTrackScoreTEST() {
      {
         try {
            track = GSON.trackParser(trackConfigFile);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      assertEquals(track.calculateTrackScore(), 1);
   }

   @Test
   void calculateTrackScoreTEST2() {
      {
         try {
            track = GSON.trackParser(trackConfigFile);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      track.moveForward(4);
      assertEquals(track.calculateTrackScore(), 2);
      track.moveForward(1);
      assertEquals(track.calculateTrackScore(), 2);
      track.moveForward(2);
      assertEquals(track.calculateTrackScore(), 4);
   }


   @Test
   void checkIfCurrentPositionIsActiveTEST() {
      {
         try {
            track = GSON.trackParser(trackConfigFile);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      {
         try {
            track2 = GSON.trackParser(trackConfigFile);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      track.addToVaticanReportObserverList(track2);
      track2.addToVaticanReportObserverList(track);
      track.moveForward(8);
      assertEquals(track2.calculateTrackScore(), 1);
      assertEquals(track.calculateTrackScore(), 4+2);
      track2.moveForward(8);
      assertEquals(track.calculateTrackScore(), 4+2);
      assertEquals(track2.calculateTrackScore(), 4);
   }

   @Test
   void checkIfCurrentPositionIsActiveTEST2() {
      {
         try {
            track = GSON.trackParser(trackConfigFile);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      {
         try {
            track2 = GSON.trackParser(trackConfigFile);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      track.addToVaticanReportObserverList(track2);
      track2.addToVaticanReportObserverList(track);
      track.moveForward(7);
      track2.moveForward(8);
      track2.moveForward(8);
      assertEquals(track.calculateTrackScore(), 4+2);
      assertEquals(track2.calculateTrackScore(), 12+2+3);
   }

   @Test
   void checkIfCurrentPositionIsActiveTEST3() {
      {
         try {
            track = GSON.trackParser(trackConfigFile);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      {
         try {
            track2 = GSON.trackParser(trackConfigFile);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      track.addToVaticanReportObserverList(track2);
      track2.addToVaticanReportObserverList(track);
      track.moveForward(5);
      track2.moveForward(16);
      track2.moveForward(1);
      assertEquals(track.calculateTrackScore(), 2+2);
      assertEquals(track2.calculateTrackScore(), 12+2+3);
   }

   @Test
   void checkIfCurrentPositionIsActiveTEST4() {
      {
         try {
            track = GSON.trackParser(trackConfigFile);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      {
         try {
            track2 = GSON.trackParser(trackConfigFile);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      track.addToVaticanReportObserverList(track2);
      track2.addToVaticanReportObserverList(track);
      track.moveForward(4);
      track2.moveForward(8);
      track.moveForward(4);
      track.moveForward(4);
      track2.moveForward(8);
      assertEquals(track.calculateTrackScore(), 9);
      assertEquals(track2.calculateTrackScore(), 2+3+12);
   }
}