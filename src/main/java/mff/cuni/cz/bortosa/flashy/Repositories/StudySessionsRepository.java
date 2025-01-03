package mff.cuni.cz.bortosa.flashy.Repositories;

import java.sql.Connection;

public class StudySessionsRepository {
    private final Connection connection;

    public StudySessionsRepository(Connection connection) {
        this.connection = connection;
    }
}
