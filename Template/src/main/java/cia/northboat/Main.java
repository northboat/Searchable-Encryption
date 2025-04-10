package cia.northboat;

import cia.northboat.se.CipherSystem;
import cia.northboat.util.FileUtil;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    public static Field G1, G2, GT, Zr;
    private static Pairing bp;
    private static final int n;

    static{
        bp = PairingFactory.getPairing("a.properties");
        G1 = bp.getG1();
        G2 = bp.getG2();
        GT = bp.getGT();
        Zr = bp.getZr();
        n = 26;
    }

    public static void main(String[] args) {
        System.out.println("Creat Your Cipher Implement by Extending CipherSystem in Directory se! The functions including setup, keygen, enc, trap, search and test are supposed to be finished");
        System.out.println("In this Main module, you should create a List<CipherSystem> and add your system to it then use executorServiceTest() to count its cost");
    }
	
	

    public static void logTime(){
        FileUtil.writeCostToLog("================= Time Cost =================");
        for(List<Long> t: times){
            for(long i: t){
                FileUtil.writeCostToLog(i + "\t\t\t");
            }
            FileUtil.writeCostToLog("\n");
        }
        FileUtil.writeCostToLog("\n\n");
    }
	
	
	public static void executorServiceTest(List<CipherSystem> cipherSystems, List<String> words,
                            int sender, int receiver, int round){
        int n = cipherSystems.size();

        // 需要测试的算法数量
        List<List<Long>> times = new ArrayList<>(n);
        for(int i = 0; i < n; i++){
            times.add(new ArrayList<>());
        }
        ExecutorService executor = Executors.newFixedThreadPool(n);
        List<Future<List<Long>>> futures = new ArrayList<>();
        // 提交任务
        for(CipherSystem cipherSystem: cipherSystems){
            futures.add(executor.submit(() -> cipherSystem.test(words, sender, receiver, round)));
        }

        // 获取结果
        try {
            // 这一步是阻塞的，用 set 保证各算法先后次序是我所希望的
            for(int i = 0; i < n; i++){
                Future<List<Long>> future = futures.get(i);
                times.set(i, future.get());
            }
            // 记录结果
            logTime(times);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            // 关闭线程池
            executor.shutdown();
        }
    }
}