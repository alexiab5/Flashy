    package mff.cuni.cz.bortosa.flashy.Scenes;

    public enum SceneType {
        MAIN_MENU("/Fxml/Main-View.fxml"),
        ADD_FLASHCARD("/Fxml/Flashcards-View.fxml"),
        ADD_DECK("/Fxml/Deck-View.fxml"),
        BROWSE("/Fxml/Browse-View.fxml"),
        STUDY_SESSION("/Fxml/StudySession-View.fxml"),
        STATISTICS("/Fxml/Statistics-View.fxml");
        //ADD_QUIZ, QUIZ_MODE

        private String path;

        private SceneType(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }

    }
