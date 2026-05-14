module com.example.regret_it {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;


    opens com.example.regret_it to javafx.fxml;
    exports com.example.regret_it;
}