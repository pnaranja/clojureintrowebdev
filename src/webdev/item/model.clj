(ns webdev.item.model
  (:require [clojure.java.jdbc :as db])
  )

(defn create-table "Create items table if it does not exist"
  [db] 
  (db/execute! db ["CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\""])
  (db/execute! db ["CREATE TABLE IF NOT EXISTS items
                   (id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                    name TEXT NOT NULL,
                    description TEXT NOT NULL,
                    checked BOOLEAN NOT NULL DEFAULT FALSE,
                    date_created TIMESTAMPTZ NOT NULL DEFAULT now())"])
  )

(defn create-item "Create item with it's name and description.  Return row id"
  [db name description]
  (:id (first (db/query
                db
                ["INSERT INTO items (name,description)
                 VALUES (?,?)
                 RETURNING id"
                 name description]))))

(defn update-item "Update item's checked flag.  Return true if update was successful"
  [db id checked]
  (= [1] (db/execute! db ["UPDATE items SET checked = ? WHERE id = ?"
                          checked id])))

(defn delete-item "Delete an item given the id. Return true if delete was successful"
  [db id]
  (= [1] (db/execute! db ["DELETE from items WHERE id = ?" id])))

(defn read-items "Read all the items in the table"
  [db]
  (db/query db ["SELECT * FROM items ORDER BY date_created"]))
