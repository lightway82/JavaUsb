package org.anantacreative.javausb.Biofon;


import java.util.Arrays;

public class Test
{



    public static  BiofonBinaryFile testData() throws BiofonComplex.MaxTimeByFreqBoundException, BiofonComplex.MaxPauseBoundException, BiofonProgram.MaxProgramIDValueBoundException, BiofonProgram.MaxFrequenciesBoundException, BiofonProgram.MinFrequenciesBoundException, BiofonComplex.MaxCountProgramBoundException {

        BiofonComplex bc1 = new BiofonComplex(5,1);
        BiofonComplex bc2 = new BiofonComplex(5,2);
        BiofonComplex bc3 = new BiofonComplex(5,3);
        bc1.addProgram(new BiofonProgram(Arrays.<Double>asList(10.0, 100.0, 1000.0, 22.0, 220.0, 2222.0, 257.0),1));
        bc1.addProgram(new BiofonProgram(Arrays.<Double>asList(10.0, 100.0, 1000.0, 22.0, 220.0, 2222.0, 257.0),2));
        bc1.addProgram(new BiofonProgram(Arrays.<Double>asList(10.0, 100.0, 1000.0, 22.0, 220.0, 2222.0, 257.0),3));

        bc2.addProgram(new BiofonProgram( Arrays.<Double>asList(33.0,330.0),8));
        bc2.addProgram(new BiofonProgram(Arrays.<Double>asList(10.0, 100.0, 1000.0, 22.0, 220.0, 2222.0, 257.0),4));
        bc2.addProgram(new BiofonProgram( Arrays.<Double>asList(33.0,330.0),0));

        bc3.addProgram(new BiofonProgram(Arrays.<Double>asList(10.0, 100.0, 1000.0, 22.0, 220.0, 2222.0, 257.0),5));
        bc3.addProgram(new BiofonProgram( Arrays.<Double>asList(33.0,330.0),7));
        bc3.addProgram(new BiofonProgram(Arrays.<Double>asList(10.0, 100.0, 1000.0, 22.0, 220.0, 2222.0, 257.0),6));

        BiofonBinaryFile binaryFile=new BiofonBinaryFile(bc1,bc2,bc3);

            return binaryFile;
        }




}
