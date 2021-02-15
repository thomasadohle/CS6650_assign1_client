public class PhaseBlocker {

    private boolean canStartPhaseTwo = false;
    private boolean canStartPhaseThree = false;
    private static final PhaseBlocker instance = new PhaseBlocker();

    private PhaseBlocker(){}

    public static PhaseBlocker getInstance(){return instance;}

    public boolean phaseTwo(){return this.canStartPhaseTwo;}

    public boolean phaseThree(){return this.canStartPhaseThree;}

    public void startPhaseTwo(){
        this.canStartPhaseTwo = true;
        notifyAll();
    }

    public void startPhaseThree(){
        this.canStartPhaseThree = true;
        notifyAll();
    }

}
