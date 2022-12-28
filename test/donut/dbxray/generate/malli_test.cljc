(ns donut.dbxray.generate.malli-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [donut.dbxray.generate.malli :as ddgm]
   [donut.dbxray.fixtures :as ddf]))

(deftest generates-malli-specs
  (testing "qualified generate malli specs"
    (is (= '[(def User
               [:map
                [:users/id {:optional false} pos-int?]
                [:users/username {:optional false} string?]])
             (def TodoList
               [:map
                [:todo_lists/id {:optional false} pos-int?]
                [:todo_lists/created_by_id {:optional true} pos-int?]])
             (def Todo
               [:map
                [:todos/id {:optional false} pos-int?]
                [:todos/todo_list_id {:optional true} pos-int?]
                [:todos/todo_title {:optional false} string?]
                [:todos/notes {:optional true} string?]
                [:todos/created_by_id {:optional true} pos-int?]
                [:todos/created_at {:optional true} inst?]])]
           (ddgm/generate ddf/todo-list-xray {:unqualified-column false}))))
  (testing "unqualified generate malli specs"
    (is (= '[(def User
               [:map
                [:id {:optional false} pos-int?]
                [:username {:optional false} string?]])
             (def TodoList
               [:map
                [:id {:optional false} pos-int?]
                [:created_by_id {:optional true} pos-int?]])
             (def Todo
               [:map
                [:id {:optional false} pos-int?]
                [:todo_list_id {:optional true} pos-int?]
                [:todo_title {:optional false} string?]
                [:notes {:optional true} string?]
                [:created_by_id {:optional true} pos-int?]
                [:created_at {:optional true} inst?]])]
           (ddgm/generate ddf/todo-list-xray {:unqualified-column true})))))
