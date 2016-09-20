package ids;
import java.io.*;
import java.util.StringTokenizer;
import Jama.Matrix;
import ids.MCA;
import org.apache.commons.lang3.time.StopWatch;
public class MCATest {
	static BufferedReader BR;
	static PrintWriter PW_normal,PW_mdv;
	static String input_dataset="/home/fortress/workspace/CEP based hybrid IDS/CEP_Module/src/ids/gureKddcup6percent.arff";
	static String nr_output="/home/fortress/workspace/CEP based hybrid IDS/CEP_Module/src/ids/normal_records";
	static Matrix features;
	static int excluded_features[];
	static MCA mca;
	static String services[] = new String[]{"telnet","finger","ftp","other","ftp_data","login","shell","http","X11","eco","smtp","ntp","auth","domain","http_alt","pop_3","ircu","time","netbios_dmg","urh","snmp","urp","tim"};
	/**
	 * @param args
	 * @throws IOException
	 */
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String args[]) throws IOException {
		StopWatch timer = new StopWatch();
		timer.start();
		System.out.println("TIME - "+timer.getTime());
		BR = new BufferedReader(new FileReader(input_dataset));
		File nf = new File(nr_output);
		if(!nf.exists() && !nf.isDirectory())
		{
		    nf.createNewFile();
		    System.out.println("File "+nr_output+" not found, so I created it!");
		}
		File mdv = new File("md_values");
		if(!mdv.exists() && !mdv.isDirectory())
		{
		    mdv.createNewFile();
		    System.out.println("File md_values not found, so I created it!");
		}
		PW_normal = new PrintWriter(new BufferedWriter(new FileWriter(nf)));
		PW_mdv = new PrintWriter(new BufferedWriter(new FileWriter(mdv)));
		String temp;
		int total_count=0,nr_count=0,feature_count=0;;
		while((temp = BR.readLine()) != null) {
			if(temp.endsWith("normal")) {
				PW_normal.println(temp);
				nr_count++;
				feature_count = new StringTokenizer(temp,",").countTokens();
			}
			total_count++;
		}
		nr_count = 10000;
		excluded_features = new int[]{4,5,7,8,9,feature_count-1};
		System.out.println("Number of normal records - "+nr_count);
		System.out.println("Total number of records - "+total_count);
		System.out.println("Feature Count - "+feature_count);
		BR.close();
		System.out.println("Excluded features - "+excluded_features.length);
		feature_count = feature_count - excluded_features.length;
		features = new Matrix(nr_count, feature_count); // All records
		BR = new BufferedReader(new FileReader(nr_output));
		StringTokenizer record;
		int n=0,f=0,tf_index=0;
		int lv=0;
		while(lv++<10000 && (temp = BR.readLine()) != null) {
			//System.out.println(lv);
			f=0;
			tf_index=0;
			record = new StringTokenizer(temp,",");
			while(record.hasMoreTokens()) {
				if(!contains(tf_index,excluded_features)) {
					features.set(n, f, Double.parseDouble(record.nextToken()));
					f++;
				}
				else {
					record.nextToken();
				}
				tf_index++;
			}
			n++;
		}
		System.out.println(feature_count);
		System.out.println("Matrix generated....\nDimensions are : "+features.getRowDimension()+"x"+features.getColumnDimension());
		System.out.println("Features for 1st record - ");
		for(int i=0;i<features.getColumnDimension();i++) {
			//System.out.println("Feature no "+(i+1)+" - "+features.get(0, i));
		}
		System.out.println("Row Dimension"+features.getRowDimension()+", "+features.getColumnDimension());
		mca = new MCA(features,features.getRowDimension()/*.getMatrix(0, 0, 0, features.getColumnDimension()-1),10*/);
		mca.computeTAM();
		for(int i=0;i<10/*features.getRowDimension()*/;i++) {
			mca.setFeatures(features.getMatrix(i, i, 0, features.getColumnDimension()-1));
			mca.computeTAM();
			mca.computeTAMMean();
			System.out.println("TAM Mean Computed..");
			mca.computeMD();
			System.out.println("MD Computed..");
		}

		//mca.normalizeTAMaggregate();
		mca.computeCovMatrix();
		System.out.println("CovMatrix Computed..");

		mca.computeMDMean();
		System.out.println("MD Mean Computed..");
		mca.computeMDSD();
		System.out.println("MD SD Computed..\n\n\n");
		
		BR = new BufferedReader(new FileReader("/home/fortress/workspace/CEP based hybrid IDS/CEP_Module/src/ids/gureKddcup6percent_headers_removed.arff"));
		boolean is_anomaly = false;
		int test_index = 0;
		Matrix f_vector = new Matrix(1, feature_count);
		int i =0;
		int FP=0,FN=0,CD=0;//False Positives, False Negatives, Correct Detection.
		int rcount =0;
		while((temp = BR.readLine()) != null) {
			rcount++;
			record = new StringTokenizer(temp,",");
			test_index = 0;
			i=0;
			while(record.hasMoreTokens()) {
				if(!contains(test_index,excluded_features)) {
					f_vector.set(0, i, Double.parseDouble(record.nextToken()));
					i++;
				}
				else {
					record.nextToken();
				}
				test_index++;
			}
			mca.setFeatures(f_vector);
			mca.computeTAM();	
			mca.computeTAMMean();
			//mca.computeCovMatrix();
			double temp_md = mca.computeMD();
			PW_mdv.println(temp_md);
			is_anomaly = mca.isAnomaly();
			if(((!is_anomaly) && temp.endsWith("normal")) || ((is_anomaly) && !temp.endsWith("normal"))) {
				CD++;
			}
			else if((is_anomaly) && temp.endsWith("normal")) {
				FP++;
			}
			else if((!is_anomaly) && !temp.endsWith("normal")) {
				FN++;
			}

		if(rcount%10000 == 0) {
			System.out.println("Detection Accuracy - "+CD+" - "+(CD/(double)rcount));		
			System.out.println("False Positives - "+FP+" - "+(FP/(double)rcount));
			System.out.println("False Negatives - "+FN+" - "+(FN/(double)rcount));
			System.out.println("TIME - "+timer.getTime());	
		}
		
	}
		System.out.println("Detection Accuracy - "+CD+" - "+(CD/(double)rcount));		
		System.out.println("False Positives - "+FP+" - "+(FP/(double)rcount));
		System.out.println("False Negatives - "+FN+" - "+(FN/(double)rcount));
		System.out.println("TIME - "+timer.getTime());	
}
	
	public static boolean contains(int value, int[] list) {
		for(int val:list) {
			if(val == value) {
				return true;
			}
		}
		return false;
	}
}
