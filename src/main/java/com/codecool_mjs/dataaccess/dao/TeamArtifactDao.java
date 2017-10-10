package com.codecool_mjs.dataaccess.dao;

import com.codecool_mjs.model.Artifact;
import com.codecool_mjs.model.Quest;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TeamArtifactDao extends Dao<Artifact> {

    Artifact createObject(ResultSet results) throws SQLException {

        Integer id = results.getInt("id");
        String name = results.getString("name");
        String description = results.getString("description");
        Integer cost = results.getInt("cost");

        Artifact artifact = new Artifact(id, name, description, cost);

        return artifact;
    }

    String getQueryGetAll() {

        String query = "SELECT * FROM artifacts WHERE type = 'team'";

        return query;
    }

    String getQuerySearchBy(String category, String arg) {

        String query = String.format("SELECT * FROM artifacts WHERE %s LIKE '%s' AND type = 'team'", category, arg);

        return query;
    }

    @Override
    Integer executeInsertation(Artifact artifact) throws SQLException {
        return null;
    }

    @Override
    Integer executeDeletion(Artifact artifact) throws SQLException {
        return null;
    }
}