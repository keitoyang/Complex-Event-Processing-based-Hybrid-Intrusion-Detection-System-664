/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids.cep;
import java.util.List;
import ids.deployment.Device;
import ids.deployment.Host;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import java.io.PrintStream;
import org.drools.runtime.StatefulKnowledgeSession;
import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.NetInfo;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.Sigar;

/**
 *
 * @author root
 * Source : http://www.tutorialspoint.com/drools/drools_sample_drools_program.htm
 */
public class CEP extends Thread{

    String RuleFiles[];
    List<Device> devices;
    boolean iterative;
    public CEP(String RuleFiles[],List<Device> devices2,boolean iterative) {
        this.RuleFiles = RuleFiles;
        this.devices = devices2;
        this.iterative = iterative;
    }
            
    public void run() {
        startCEPSession();
    }
    
    public void startCEPSession() {
        StatefulKnowledgeSession ks = buildKnowledgeBase(RuleFiles, System.out).newStatefulKnowledgeSession();
        
        if(iterative) {
            while(true) {
                for(Device device:devices) {
                    ks.insert(device);
                }
                ks.fireAllRules();
                try {
                Thread.sleep(1000);}
                catch(Exception e) {
                    System.err.println(e);
                }
            }
        }
        else {
            for(Device device:devices) {
                    ks.insert(device);
            }
            ks.fireAllRules();

        }
        
        System.out.println("CEP Engine Exiting...");
        
    }
    
    public KnowledgeBase buildKnowledgeBase(String knowledge[], PrintStream output) {
        KnowledgeBuilder kbu = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for (String file : knowledge) {
            kbu.add(ResourceFactory.newClassPathResource(file), ResourceType.DRL);
        }
        KnowledgeBuilderErrors errors = kbu.getErrors();
        if (errors.size() > 0) {
            for(KnowledgeBuilderError error:errors) {
                output.println(error.toString());
            }
        }
        KnowledgeBase kba = KnowledgeBaseFactory.newKnowledgeBase();
        kba.addKnowledgePackages(kbu.getKnowledgePackages());
        return kba;
    }
}
