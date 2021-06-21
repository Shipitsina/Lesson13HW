import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Car implements Runnable {
    private static boolean hasWinner = false;
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private final CyclicBarrier cyclicBarrier;
    private final CountDownLatch countDownLatch1;
    private final CountDownLatch countDownLatch2;
    private static int CARS_COUNT;
    private Race race;
    private int speed;
    private String name;
    public String getName() {
        return name;
    }
    public int getSpeed() {
        return speed;
    }
    public Car(Race race, int speed, CyclicBarrier cyclicBarrier, CountDownLatch countDownLatch1, CountDownLatch countDownLatch2) {

        this.race = race;
        this.speed = speed;
        this.cyclicBarrier = cyclicBarrier;
        this.countDownLatch1 = countDownLatch1;
        this.countDownLatch2 = countDownLatch2;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
    }
    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int) (Math.random() * 800));
            System.out.println(this.name + " готов");
            countDownLatch1.countDown();
            cyclicBarrier.await(10, TimeUnit.SECONDS);
            for (int i = 0; i < race.getStages().size(); i++) {
                race.getStages().get(i).go(this);
                if (( i == race.getStages().size()-1) && !hasWinner){
                    try {
                        lock.writeLock().lock();
                        System.out.println(this.name + " - WIN!!!");
                        hasWinner = true;
                    } finally {
                        lock.writeLock().unlock();
                    }
                }
            }
            countDownLatch2.countDown();
        } catch (BrokenBarrierException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}