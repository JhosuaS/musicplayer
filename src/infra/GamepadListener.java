package infra;

public interface GamepadListener {
    
    void onStop();          // a
    void onNext();          //dpadRight
    void onPrevious();      //dpadleft
    void onVolUp();         //RB
    void onVolDown();       //LB
    void onPreviousPag();   //y
    void onSelect();        // x
    void onCrossUp();       //dpadUp
    void onCrossDown();     //dpadDown

}