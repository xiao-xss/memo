package xiao.xss.study.tool;

import java.util.UUID;

/**
 * 自定义可暂停、重启，可执行定制化业务的Thread
 *
 * @author xiaoliang
 * @since 2018/7/11 18:36
 */
public abstract class ProxyThread implements Runnable {
    private enum STATUS {
        RUNNING, SUSPEND, STOP;
        boolean isRunning(){ return this == RUNNING;}
        boolean isSuspended(){ return this == SUSPEND;}
        boolean isStop(){ return this == STOP;}
    }
    private Thread thread;
    private volatile STATUS status = STATUS.RUNNING;
    private final String control = "";
    private long awaitMs; // 定制业务执行间隔
    private String name;

    ProxyThread(long awaitMs, String name) {
        this.awaitMs = awaitMs;
        this.name = name;
        if(this.name == null || this.name.length() == 0) {
            String suffix = UUID.randomUUID().toString().substring(0, 8);
            this.name = "ProxyThread-" + suffix;
        }
    }
    ProxyThread(long awaitMs) {
        this(awaitMs, null);
    }

    @Override
    public final void run() {
        while(!this.status.isStop()) {
            if(this.status.isSuspended()) {
                synchronized(this.control) {
                    try {
                        this.control.wait();
                    } catch(InterruptedException e) {
                        // just weak up early
                    }
                }
            } else {
                execute();
                if(this.awaitMs > 0) sleep(this.awaitMs);
            }
        }
        System.out.println(String.format("Proxy thread %s is executed", this.name));
    }

    public abstract void execute();

    /**
     * 启动线程
     *
     * @param run true：启动后立即执行定制业务，false：启动后暂停线程
     */
    public final void start(boolean run) {
        System.out.println(String.format("Start proxy thread %s", this.name));
        if(this.thread == null || !this.name.equals(this.thread.getName())) {
            this.thread = new Thread(this, this.name);
        }
        if(!this.thread.isAlive()) {
            this.status = STATUS.SUSPEND;
            this.thread.start();
            // wait for thread start
            sleep(100L);
        }
        if(run) resume();
    }

    /**
     * 暂停线程
     */
    public final void suspend() {
        System.out.println(String.format("Suspend proxy thread %s", this.name));
        checkStatus();
        if(this.status.isRunning()) {
            this.status = STATUS.SUSPEND;
        }
    }

    /**
     * 继续执行定制业务
     */
    public final void resume(){
        System.out.println(String.format("Resume proxy thread %s", this.name));
        checkStatus();
        STATUS oldStatus = this.status;
        this.status = STATUS.RUNNING;
        if(oldStatus.isSuspended()) {
            synchronized(this.control) {
                this.control.notify();
            }
        }
    }

    /**
     * 停止线程
     */
    public final void stop() {
        System.out.println(String.format("Stop proxy thread %s", this.name));
        if(this.status.isStop()) return;
        STATUS oldStatus = this.status;
        this.status = STATUS.STOP;
        if(oldStatus.isSuspended()) {
            synchronized(this.control) {
                this.control.notify();
            }
        }
        this.thread = null;
    }

    private void checkStatus() {
        if(this.status.isStop()) throw new IllegalThreadStateException(String.format("Proxy thread %s is not running", this.name));
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch(InterruptedException e) {
            // just wake up early
        }
    }

    public static void main(String[] args) {
        ProxyThread proxy = new ProxyThread(1000L) {
            @Override
            public void execute() {
                System.out.print("*");
            }
        };
        proxy.start(true);
        proxy.sleep(5000L);
        proxy.resume();
        proxy.sleep(10000L);
        proxy.suspend();
        proxy.sleep(5000L);
        proxy.stop();
    }
}
