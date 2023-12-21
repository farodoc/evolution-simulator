package agh.ics.oop;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimulationEngine {
    private final List<Simulation> simulations;
    private final ExecutorService executorService;
    private final List<Thread> threads = new ArrayList<>();

    public SimulationEngine(List<Simulation> simulations, int n) {
        this.executorService = Executors.newFixedThreadPool(n);
        this.simulations = simulations;
    }

    public void runAsyncInThreadPool() {
        for (Simulation simulation : simulations) {
            executorService.submit(simulation);
        }
    }

    public void runSync() {
        for (Simulation simulation : simulations) {
            System.out.println("Running simulation");
            simulation.run();
            System.out.println("Simulation finished.\n");
        }
    }

    public void runAsync() {
        for (Simulation simulation : simulations) {
            Thread thread = new Thread(simulation);
            thread.start();
            threads.add(thread);
        }
    }

    public void awaitSimulationsEnd() {
        if(threads.isEmpty()){
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }
        else{
            for(Thread thread : threads){
                try{
                    thread.join();
                }
                catch (InterruptedException e){
                    System.err.println("The thread has been interrupted: " + e.getMessage());
                    break;
                }
            }
        }
    }
}