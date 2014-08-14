#!/usr/bin/env boot

#tailrecursion.boot.core/version "2.5.1"

(set-env!
  :project      'hopfuse
  :version      "0.1.0-SNAPSHOT"
  :dependencies '[[tailrecursion/boot.task "2.2.4"]
                  [tailrecursion/hoplon "5.10.21"]
                  [com.datomic/datomic-free "0.9.4815.12"]]
  :out-path     "resources/public"
  :src-paths    #{"src/hl" "src/cljs" "src/clj", "src/resource"})

;; Static resources (css, images, etc.):
(add-sync! (get-env :out-path) #{"assets"})

(require '[tailrecursion.hoplon.boot :refer :all]
         '[tailrecursion.castra.task :as c]
         '[hopfuse.tasks :as my])

(deftask development
  "Build hopfuse for development."
  []
  (comp (my/nrepl {:port 33133}) (watch) (hoplon {:prerender false}) (c/castra-dev-server 'hopfuse.api)))

(deftask production
  "Build hopfuse for production."
  []
  (hoplon {:optimizations :advanced}))
