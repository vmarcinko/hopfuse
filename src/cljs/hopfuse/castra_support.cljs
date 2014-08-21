(ns hopfuse.castra-support
  (:require-macros
    [tailrecursion.javelin :refer [defc defc= cell=]])
  (:require
   [tailrecursion.javelin]
   [tailrecursion.castra :refer [async *url* safe-pop]]))

(defn mkremote [endpoint state error loading & [url ajax-impl]]
  (let [url (or url *url* (.. js/window -location -href))]
    (fn [args & {:keys [success fail]}]
      (swap! loading conj :tailrecursion.castra/xhr)
      (async
        url
        `[~endpoint ~@args]
        #(do (reset! error nil)
             (reset! state %)
             (when success
                 (success)))
        #(do (reset! error %)
             (when fail
               (fail)))
        #(swap! loading safe-pop)
        :ajax-impl ajax-impl))))
