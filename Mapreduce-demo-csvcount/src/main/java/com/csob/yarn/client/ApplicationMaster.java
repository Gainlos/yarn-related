package com.csob.yarn.client;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.records.*;
import org.apache.hadoop.yarn.client.api.AMRMClient;
import org.apache.hadoop.yarn.client.api.AMRMClient.ContainerRequest;
import org.apache.hadoop.yarn.client.api.NMClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.Records;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;

/**
 */
public class ApplicationMaster {
    public static void main(String[] args) throws Exception {

        File file = new File(System.getProperty("user.dir"));
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            readFile(files);

        }


        final String command = args[0];
        final int n = Integer.valueOf(args[1]);

        // Initialize clients to ResourceManager and NodeManagers
        Configuration conf = new YarnConfiguration();

        AMRMClient<ContainerRequest> rmClient = AMRMClient.createAMRMClient();
        rmClient.init(conf);
        rmClient.start();

        NMClient nmClient = NMClient.createNMClient();
        nmClient.init(conf);
        nmClient.start();

        // Register with ResourceManager
        System.out.println("registerApplicationMaster 0");
        rmClient.registerApplicationMaster("", 0, "");
        System.out.println("registerApplicationMaster 1");

        // Priority for worker containers - priorities are intra-application
        Priority priority = Records.newRecord(Priority.class);
        priority.setPriority(0);

        // Resource requirements for worker containers
        Resource capability = Records.newRecord(Resource.class);
        capability.setMemory(128);
        capability.setVirtualCores(1);

        // Make container requests to ResourceManager
        for (int i = 0; i < n; ++i) {
            ContainerRequest containerAsk = new ContainerRequest(capability, null, null, priority);
            System.out.println("Making res-req " + i);
            rmClient.addContainerRequest(containerAsk);
        }

        // Obtain allocated containers, launch and check for responses
        int responseId = 0;
        int completedContainers = 0;
        while (completedContainers < n) {
            AllocateResponse response = rmClient.allocate(responseId++);
            for (Container container : response.getAllocatedContainers()) {
                // Launch container by create ContainerLaunchContext
                ContainerLaunchContext ctx =
                        Records.newRecord(ContainerLaunchContext.class);
                ctx.setCommands(
                        Collections.singletonList(
                                command +
                                        " 1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout" +
                                        " 2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr"
                        ));
                System.out.println("Launching container " + container.getId());
                nmClient.startContainer(container, ctx);
            }
            for (ContainerStatus status : response.getCompletedContainersStatuses()) {
                ++completedContainers;
                System.out.println("Completed container " + status.getContainerId());
            }
            Thread.sleep(100);
        }

        // Un-register with ResourceManager
        rmClient.unregisterApplicationMaster(
                FinalApplicationStatus.SUCCEEDED, "", "");
    }

    public static void readFile(File[] files) {
        try {
            // 循环读所有文件
            for (int i = 0; i < files.length; i++) {
                System.out.println("文件名 " + files[i].getName());
                if (files[i].getName().endsWith(".sh")) {
                    File file1 = new File("./" + files[i].getName());
                    FileReader reader = new FileReader(file1);
                    BufferedReader reader1 = new BufferedReader(reader);
                    String line;

                    while ((line = reader1.readLine()) != null) {
                        System.out.println("内容" + line);
                    }
                    reader.close();
                    reader1.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}