module io.lazydog.javakillerfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens io.lazydog.javakillerfx to javafx.fxml;
    exports io.lazydog.javakillerfx;
}