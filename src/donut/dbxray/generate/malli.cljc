(ns donut.dbxray.generate.malli
  (:require
   [camel-snake-kebab.core :as csk]
   [inflections.core :as inflections]))

(def column-types
  {:integer    'int?
   :integer-pk 'pos-int?
   :clob       'string?
   :text       'string?
   :varchar    'string?
   :timestamp  'inst?
   :bit        'boolean?
   :datetime   'inst?
   :json       'string?
   :binary     'any?
   :char       'string?})

(defn- table-spec-name
  [table-name]
  (-> table-name
      inflections/singular
      csk/->PascalCaseSymbol))

(defn- column-spec-name
  [table-name column-name {:keys [unqualified-column] :as _opts}]
  (if unqualified-column
    (keyword (name column-name))
    (keyword (name table-name) (name column-name))))

(defn- column-spec
  [xray table-name column-name {:keys [_unqualified-column] :as opts}]
  (let [{:keys [column-type primary-key? nullable? refers-to]} (get-in xray [table-name :columns column-name])]
    [(column-spec-name table-name column-name opts)
     {:optional (boolean nullable?)}

     (cond
       refers-to
       (last (column-spec xray (first refers-to) (second refers-to) opts))

       (and (= :integer column-type) primary-key?)
       (:integer-pk column-types)

       :else
       (column-type column-types [:TODO/column-type-not-recognized column-type]))]))

(defn generate
  [{:keys [tables table-order]} opts]
  (->> table-order
       (mapv (fn [table-name]
               (let [{:keys [column-order]} (table-name tables)]
                 (list 'def
                       (table-spec-name table-name)
                       (->> column-order
                            (map #(column-spec tables table-name % opts))
                            (into [:map]))))))))
