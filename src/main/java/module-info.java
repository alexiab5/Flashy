module mff.cuni.cz.bortosa.flashy {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens mff.cuni.cz.bortosa.flashy to javafx.fxml;
    exports mff.cuni.cz.bortosa.flashy;
    exports mff.cuni.cz.bortosa.flashy.Controllers;
    opens mff.cuni.cz.bortosa.flashy.Controllers to javafx.fxml;
    exports mff.cuni.cz.bortosa.flashy.DatabaseOperations;
    opens mff.cuni.cz.bortosa.flashy.DatabaseOperations to javafx.fxml;
    exports mff.cuni.cz.bortosa.flashy.Scenes;
    opens mff.cuni.cz.bortosa.flashy.Scenes to javafx.fxml;
}