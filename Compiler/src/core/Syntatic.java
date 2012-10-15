package core;

public class Syntatic implements Runnable {

    private static Syntatic instance;
    private Core core;

    private Syntatic() {

    }

    public static Syntatic getInstance() {
        if (instance == null) {
            instance = new Syntatic();
        }

        return instance;
    }

    public void analyze() {
        do {
            new Thread(core).start();
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(core.getToken().getLexeme());
        } while (core.getToken() != null);
    }

    @Override
    public void run() {
        analyze();
    }

    public void setCore(Core c) {
        core = c;
    }
}
