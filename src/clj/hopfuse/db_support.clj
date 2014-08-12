(ns hopfuse.db-support
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [datomic.api :as d]))

(def ^:private datomic-tag-readers {'db/id datomic.db/id-literal
                                    'db/fn datomic.function/construct
                                    'base64 datomic.codec/base-64-literal})

(defn now [] (java.util.Date.))

(defn reify-entity-from-id-tuple [db]
  (fn [tuple]
    (d/entity db (first tuple))))

(defn rename-selected-keys [m key-replacements]
  (when m
    (let [selected-map (select-keys m (keys key-replacements))
         new-keys (replace key-replacements (keys selected-map))]
     (zipmap new-keys (vals selected-map)))))

(defn- initialize-db! [conn]
  (let [initial-txs (edn/read-string {:readers datomic-tag-readers} (slurp (io/resource "datomic/schema.edn")))]
    (doseq [initial-tx initial-txs]
      @(d/transact conn initial-tx))))

(defn- create-empty-in-memory-db []
  (let [uri "datomic:mem://myapplication"]
  ;(let [uri "datomic:free://localhost:4334/pet-owners-test"]
    (d/delete-database uri)
    (d/create-database uri)
    (let [conn (d/connect uri)]
      (initialize-db! conn)
      conn)))

(def conn (create-empty-in-memory-db))

(defn get-last-db [] (d/db conn))
