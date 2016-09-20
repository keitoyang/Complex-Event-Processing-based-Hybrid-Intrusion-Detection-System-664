/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;
import ids.deployment.Network;
import ids.deployment.Host;
import Jama.Matrix;
/**
 *
 * @author Ranjan mohan
 * @author Kun Yang
 * @version 1.1
 */
public class MCA {
        private int FeatureCount,RecordCount,RecordNumber;
        private double MDMean,MDSD,CovDeterminant;
        private boolean TAMAggregateFull;
        Matrix FeatureVector_temp,TAMAggregate,NormalizedTAMAggregate,TAMAggregateMax,TAMMean,TAMNormalizedMean,CovarianceMatrix;
        private double[] MD;
        private double alpha;
        /**
         * Initialize the variable needed for MCA Computation
         */
        public void initialize(int record_count){
        	System.out.println("MCA - Max Heap Size - "+Runtime.getRuntime().totalMemory());
            RecordCount = record_count;
            RecordNumber = 0;
            TAMAggregate = new Matrix(FeatureCount*(FeatureCount-1)/2,RecordCount);
            System.out.println("Normalized TAM Aggregate - Dimensions : "+TAMAggregate.getRowDimension()+"x"+TAMAggregate.getColumnDimension()+" - Size : ");
            NormalizedTAMAggregate = new Matrix(FeatureCount*(FeatureCount-1)/2,RecordCount);
            TAMMean = new Matrix(FeatureCount*(FeatureCount-1)/2,1);
            TAMNormalizedMean = new Matrix(FeatureCount*(FeatureCount-1)/2,1);
            TAMAggregateMax = new Matrix(FeatureCount*(FeatureCount-1)/2,1);
            CovarianceMatrix = new Matrix(RecordCount,RecordCount);
            TAMAggregateFull = false;
            CovDeterminant = 1.0d;
            MD = new double[RecordCount];
            alpha = 0.5d;
        }
        /**
         * To accept and initialize the input parameters required for MCA
         * @param host The Host to perform MCA on 
         */
        public MCA(Host host,int record_count) {
            FeatureCount = host.getFeaturesCount();
            initialize(record_count);
            FeatureVector_temp=host.getFeatureVector();

        }
        /**
         * To accept and initialize the input parameters required for MCA
         * @param nw The Network to perform MCA on 
         */
        public MCA(Network nw,int record_count) {
        	FeatureCount = nw.getFeaturesCount();
        	initialize(record_count);
        	FeatureVector_temp=nw.getFeatureVector();
        
        }

        public MCA(Matrix features,int record_count) {
            FeatureCount = features.getColumnDimension();
            initialize(record_count);
            FeatureVector_temp=features;

        } 
        public void setParameters(double MDMean,double MDSD,double alpha) {
            this.MDMean = MDMean;
            this.alpha = alpha;
            this.MDSD = MDSD;

        }
       public static void main(String h[])
        {
        //	new MCA().MCA(new Host());
        }
        /**
         * To set the features of MCA
         * @param host Host Object whose features are to be taken
         * @return Exit STatus
         */
        public int setFeatures(Host host) {
        	FeatureVector_temp=host.getFeatureVector();
        	return 0;
        }
        /**
         * To set the features of MCA
         * @param nw Network Object whose features are to be taken
         * @return Exit STatus
         */
        public int setFeatures(Network nw) {
        	FeatureVector_temp=nw.getFeatureVector();
        	return 0;
        }
        
        public int setFeatures(Matrix features) {
        	FeatureVector_temp=features;
        	return 0;
        }
        
        /**
         * Compute the Triangle Area Maps 
         * @return Exit Status
         */
        public int computeTAM() {
        	
        	int count=0;
        	//System.out.println("TAM Aggregate");
        	for (int i=0;i<FeatureCount;i++) {
        		for (int j=i+1;j<FeatureCount;j++)
        		{
        			TAMAggregate.set(count, RecordNumber ,FeatureVector_temp.get(0,i)*FeatureVector_temp.get(0,j)/2.0);
        			count++;
        			//System.out.print(TAMAggregate.get(count-1, RecordNumber)+", ");
        			/*if(j<70) {
        				
        			}
        			*/
        		}
        		//System.out.println("... ");
        	}
        	//System.out.println("Record Count : "+RecordCount);
        	RecordNumber = (RecordNumber+1)%RecordCount;
        	if (RecordNumber == 0 && !TAMAggregateFull)
        	{
        		TAMAggregateFull = true;
        	}
        	else if(TAMAggregateFull)
        		TAMAggregateFull = false;
        	normalizeTAMaggregate();
        	return 0;
        }
        /**
         * Compute the mean of Triangle Area Maps
         * @return Exit STatus
         */        
        public int computeTAMMean() {
        	//System.out.println("TAM Mean");
        	for (int i=0;i<TAMAggregate.getRowDimension();i++)
        	{
        		double sum = 0;
        		for (int j=0;j<RecordCount;j++)
        			sum += TAMAggregate.get(i,j);
        		TAMMean.set(i, 0, sum/RecordCount);
        		//System.out.println("Sum - "+sum+", Mean - "+sum/RecordCount);
        	}
        	return 0;
        }
         /**
         * Normalize the Triangle Area Maps
         * @return Exit STatus
         */          
        public int normalizeTAMaggregate() {
        	
        	//System.out.println("normalizeTAMaggregate()");
        	for (int i = 0; i < TAMAggregate.getRowDimension() ; i++) {
            	
        		//Sets the maximum TAM value obtained so far for each feature
        		for (int j = 0; j <TAMAggregate.getColumnDimension(); j++)
            	{
        			TAMAggregateMax.set(i,0,0.0d);
            		if (TAMAggregate.get(i,j)>TAMAggregateMax.get(i,0))
            			TAMAggregateMax.set(i,0,TAMAggregate.get(i,j));
            		/*if(i>0) {
        				if(TAMAggregate.get(i-1,j) == 0) {
        					NormalizedTAMAggregate.set(i-1,j,0);
        				}
        				else
        					NormalizedTAMAggregate.set(i-1,j,TAMAggregate.get(i-1,j)/TAMAggregateMax.get(i-1, 0));
        			}
        			*/
            	}
            	
        		//System.out.println("Normalized TAM Aggregate");
        		//Normalizes the TAM Aggregate
            	for (int j = 0; j < RecordCount; j++) {
            		
            		//If Max TAM value is 0, correct it to 1 so that there is no Division by 0.
            		if(TAMAggregateMax.get(i, 0) == 0.0d)
            			TAMAggregateMax.set(i, 0,1.0d);
            		
            		if(TAMAggregate.get(i,j) == 0) {
            			NormalizedTAMAggregate.set(i,j,0);
            		}
            		else {
            			NormalizedTAMAggregate.set(i,j,TAMAggregate.get(i,j)/TAMAggregateMax.get(i, 0));
            			//System.out.println("TAMAgg "+i+", "+j+" - "+TAMAggregate.get(i,j));
            		}
            		
            		//System.out.print("TAgg - "+NormalizedTAMAggregate.get(i,j));
            		}
        	}
        	
				
        	
        	for (int i=0;i<NormalizedTAMAggregate.getRowDimension();i++)
        	{
        		double sum = 0.0;
        		for (int j=0;j<RecordCount;j++)
        			sum += NormalizedTAMAggregate.get(i,j);
        		TAMNormalizedMean.set(i, 0, sum/RecordCount);
        		//System.out.println("TAM Normalized Mean - "+sum);
        	}
            return 0;
        }
         /**
         * Compute Triangle Area Maps Covariance, needed by computeCovMatrix
         * @return Triangle Area Maps Covariance
         */            
        public double TAMCovariance(int TAM1,int TAM2) {
        	double sum=0.0d;
           	for(int i=0;i<NormalizedTAMAggregate.getRowDimension();i++) {
            		sum+=(NormalizedTAMAggregate.get(i, TAM1) - TAMNormalizedMean.get(i, 0) ) * (NormalizedTAMAggregate.get(i, TAM2) - TAMNormalizedMean.get(i, 0));		
            		//System.out.println("Sum within TAMCov <"+sum+">");
                    
           	}
            return sum/(NormalizedTAMAggregate.getRowDimension()-1);
        }
         /**
         * Compute the Covariance Matrix
         * @return Exit Status
         */            
        public int computeCovMatrix() {
            //System.out.println("Cov Matrix");
        	for(int i=0;i<RecordCount;i++) {
        		
        		for(int j=0;j<=i;j++) {
                    if(i==j)
                        CovarianceMatrix.set(i, j, 0);
                    else {
                        CovarianceMatrix.set(i, j, TAMCovariance(i,j));
                        CovarianceMatrix.set(j, i, TAMCovariance(i,j));
                        //System.out.println(i+", "+j+" - "+TAMCovariance(i, j));
                    }
                }
            }
            return 0;
        }
         /**
         * Compute the Mahalanobis Distance
         * @return Exit STatus
         */            
        public double computeMD() {
        	//System.out.println(TAMAggregate.getRowDimension()+" LOL");
            Matrix tem_Matrix = NormalizedTAMAggregate.getMatrix(0,TAMAggregate.getRowDimension()-1,RecordNumber,RecordNumber);
            tem_Matrix = tem_Matrix.minus(TAMNormalizedMean);
            tem_Matrix = tem_Matrix.transpose().times(tem_Matrix);
            if(CovDeterminant>0.0d)
                MD[RecordNumber] = Math.sqrt(tem_Matrix.get(0,0)/CovDeterminant);
            else
                MD[RecordNumber] = Math.sqrt(tem_Matrix.get(0,0));
            return MD[RecordNumber];
        }
         /**
         * Compute the Mean of Mahalanobis Distance
         * @return Exit STatus
         */            
        public int computeMDMean() {
            double sum = 0;
            //System.out.println("MD Values");
            for (int i = 0; i < RecordCount ; i++) {
            	sum += MD[i];
            	//System.out.println(MD[i]);
            }
            
            MDMean = sum / RecordCount;
            System.out.println("MD Mean - "+MDMean);
            return 0;
        }
         /**
         * Compute the Standard Deviation of the Mahalanobis Distance
         * @return Exit STatus
         */            
        public int computeMDSD() {
        	double sum = 0;
        	for (int i = 0; i < RecordCount ; i++) {
        		sum += Math.pow((MD[i]-MDMean),2);
        		//System.out.println(MD[i]+", "+MDMean+" - "+sum);
        	}
        	System.out.println(RecordCount);
        	MDSD=sum/RecordCount;
        	System.out.println("MD Standard Deviation - "+MDSD);
            return 0;
        }
        public boolean isAnomaly() {
        	if(MD[RecordNumber]>(MDMean+MDSD*alpha) || MD[RecordNumber]<(MDMean-MDSD*alpha))
        		return true;
        	return false;
        }
}