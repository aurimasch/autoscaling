package com.autoscaling.autoscaler;

import com.autoscaling.autoscaler.model.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

@Service
public class FileWriter {

    File avgCPUFILE = new File("C:\\Dev\\prj\\autoscaling\\autoscaler\\avg_cpu.csv");
    PrintWriter avgCPUWriter;

    File btCount = new File("C:\\Dev\\prj\\autoscaling\\autoscaler\\bt_count.csv");
    PrintWriter btCountWriter;

    File slaReader = new File("C:\\Dev\\prj\\autoscaling\\autoscaler\\sla.csv");
    PrintWriter slaWriter;

    File podCount = new File("C:\\Dev\\prj\\autoscaling\\autoscaler\\pod_count.csv");
    PrintWriter podContWriter;

    File podUpTime = new File("C:\\Dev\\prj\\autoscaling\\autoscaler\\pod_upTime.csv");
    PrintWriter podUpTimeWriter;



    @PostConstruct
    public void init(){
        try {
            avgCPUWriter = new PrintWriter(avgCPUFILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            btCountWriter = new PrintWriter(btCount);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            slaWriter = new PrintWriter(slaReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            podContWriter = new PrintWriter(podCount);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            podUpTimeWriter = new PrintWriter(podUpTime);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void writeAll(AvgCPU avgCPU, BTCount btCount, PodCount podCount, SLA sla, PodUpTime podUpTime) {
        avgCPUWriter.println(avgCPU.toCSVLine()+","+btCount.toCSVLine()+","+podCount.toCSVLine()+","+sla.toCSVLine()+","+podUpTime.toCSVLine());
        avgCPUWriter.flush();
    }


    public void writeAvgCPU(AvgCPU avgCPU) {
        avgCPUWriter.println(avgCPU.toCSVLine());
        avgCPUWriter.flush();
    }

    public void writeBTCount(BTCount btCount) {
        btCountWriter.println(btCount.toCSVLine());
        btCountWriter.flush();
    }

    public void writeSLA(SLA sla) {
        slaWriter.println(sla.toCSVLine());
        slaWriter.flush();
    }

    public void writePodCount(PodCount podCount) {
        podContWriter.println(podCount.toCSVLine());
        podContWriter.flush();
    }

    public void writePodUpTime(PodUpTime podUpTime) {
        podUpTimeWriter.println(podUpTime.toCSVLine());
        podUpTimeWriter.flush();
    }
}
