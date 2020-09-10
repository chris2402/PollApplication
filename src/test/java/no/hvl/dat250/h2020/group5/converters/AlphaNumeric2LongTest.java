package no.hvl.dat250.h2020.group5.converters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.Double;
import java.lang.Math;
import java.util.Random;

public class AlphaNumeric2LongTest {

    @Test
    public void TestEnconding(){
        String expected0 = "0";
        String expected10 = "A";
        String expected35 = "Z";

        String expected36 = "10";
        // String expected360 = "A0";
        String expected1679616 = "10000";

        AlphaNumeric2Long converter = new AlphaNumeric2Long();


        Assertions.assertEquals(
                Long.valueOf(0L),
                converter.convertToDatabaseColumn(expected0),
                String.format("String (%s) conversion failed - ", expected0)
        );

        Assertions.assertEquals(
                Long.valueOf(10L),
                converter.convertToDatabaseColumn(expected10),
                String.format("String (%s) conversion failed - ", expected10)
        );

        Assertions.assertEquals(
                Long.valueOf(35L),
                converter.convertToDatabaseColumn(expected35),
                String.format("String (%s) conversion failed - ", expected35)
        );

        Assertions.assertEquals(
                Long.valueOf(36L),
                converter.convertToDatabaseColumn(expected36),
                String.format("String (%s) conversion failed - ", expected36)
        );

        Assertions.assertEquals(
                Long.valueOf(1679616L),
                converter.convertToDatabaseColumn(expected1679616),
                String.format("String (%s) conversion failed - ", expected1679616)
        );
    }

    @Test
    public void TestDeconding(){
        Long expected0 = 0L;
        Long expected1 = 1L;
        Long expected9 = 9L;

        Long expectedA = 10L;
        Long expectedB = expectedA + 1L;
        Long expectedZ = expectedA + 25L;

        Long expected10 = expectedZ + expected1;
        Long expectedA0 = expected10 * expectedA;

        Long expectedA1 = expectedA0 + expected1;
        Long expectedAA = expectedA0 + expectedA;
        Long expectedAZ = expectedA0 + expectedZ;

        Long expectedB00 = expectedB * Double.valueOf(Math.pow(expected10, 2)).longValue();
        Long expectedBAZ = expectedB00 + expectedA0 + expectedZ;

        AlphaNumeric2Long converter = new AlphaNumeric2Long();

        Assertions.assertEquals(
                "0",
                converter.convertToEntityAttribute(expected0),
                String.format("Long (%d) conversion failed - ", expected0)
        );
        Assertions.assertEquals(
                "1",
                converter.convertToEntityAttribute(expected1),
                String.format("Long (%d) conversion failed - ", expected1)
        );
        Assertions.assertEquals(
                "9",
                converter.convertToEntityAttribute(expected9),
                String.format("Long (%d) conversion failed - ", expected9)
        );
        Assertions.assertEquals(
                "A",
                converter.convertToEntityAttribute(expectedA),
                String.format("Long (%d) conversion failed - ", expectedA)
        );
        Assertions.assertEquals(
                "Z",
                converter.convertToEntityAttribute(expectedZ),
                String.format("Long (%d) conversion failed - ", expectedZ)
        );
        Assertions.assertEquals(
                "10",
                converter.convertToEntityAttribute(expected10),
                String.format("Long (%d) conversion failed - ", expected10)
        );
        Assertions.assertEquals(
                "A0",
                converter.convertToEntityAttribute(expectedA0),
                String.format("Long (%d) conversion failed - ", expectedA0)
        );
        Assertions.assertEquals(
                "A1",
                converter.convertToEntityAttribute(expectedA1),
                String.format("Long (%d) conversion failed - ", expectedA1)
        );
        Assertions.assertEquals(
                "AA",
                converter.convertToEntityAttribute(expectedAA),
                String.format("Long (%d) conversion failed - ", expectedAA)
        );
        Assertions.assertEquals(
                "AZ",
                converter.convertToEntityAttribute(expectedAZ),
                String.format("Long (%d) conversion failed - ", expectedAZ)
        );
        Assertions.assertEquals(
                "BAZ",
                converter.convertToEntityAttribute(expectedBAZ),
                String.format("Long (%d) conversion failed - ", expectedBAZ)
        );

    }


    @Test
    public void TestEncondingDecoding(){

        Random random = new Random(1337L);
        AlphaNumeric2Long converter = new AlphaNumeric2Long();

        for (int i = 0; i < 300; i++){
            Long code = random.nextLong();
            String decoded = converter.convertToEntityAttribute(code);
            Long encoded = converter.convertToDatabaseColumn(decoded);
            Assertions.assertEquals(code, encoded);
        }

    }

}
