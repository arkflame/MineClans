package com.arkflame.mineclans.providers;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class ResultSetProcessor {
    public abstract void run(ResultSet resultSet) throws SQLException;
}
