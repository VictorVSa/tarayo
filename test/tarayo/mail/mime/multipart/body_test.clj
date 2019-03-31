(ns tarayo.mail.mime.multipart.body-test
  (:require [clojure.java.io :as io]
            [clojure.test :as t]
            [tarayo.mail.mime.multipart.body :as sut])
  (:import javax.mail.internet.MimeBodyPart))

(t/deftest make-bodypart-text-html-test
  (t/testing "string type"
    (let [part {:type "text/html" :content "<h1>hello</h1>"}
          bp (sut/make-bodypart part "UTF-8")]
      (t/is (instance? MimeBodyPart bp))

      (t/is (= "<h1>hello</h1>" ^String (.getContent bp)))
      (t/is (= "text/html; charset=UTF-8" (.getContentType bp)))))

  (t/testing "keyword type"
    (let [part {:type :text/html :content "<h1>hello</h1>"}
          bp (sut/make-bodypart part "UTF-8")]
      (t/is (instance? MimeBodyPart bp))
      (t/is (= "<h1>hello</h1>" ^String (.getContent bp)))
      (t/is (= "text/html; charset=UTF-8" (.getContentType bp))))))

(t/deftest make-bodypart-test
  (t/testing "inline"
    (let [part {:type :inline :content (io/file "project.clj")}
          bp (sut/make-bodypart part "UTF-8")]
      (t/is (instance? MimeBodyPart bp))
      (t/is (= ["text/x-clojure"] (seq (.getHeader bp "Content-Type"))))
      (t/is (= "project.clj" (.getFileName bp)))
      (t/is (= "inline" (.getDisposition bp)))))

  (t/testing "specify content-type"
    (let [part {:type :inline :content (io/file "project.clj")
                :content-type "text/plain"}
          bp (sut/make-bodypart part "UTF-8")]
      (t/is (instance? MimeBodyPart bp))
      (t/is (= ["text/plain"]
               (seq (.getHeader bp "Content-Type"))))))

  (t/testing "attachment"
    (let [part {:type :attachment :content (io/file "project.clj")}
          bp (sut/make-bodypart part "UTF-8")]
      (t/is (instance? MimeBodyPart bp))
      (t/is (= ["text/x-clojure"] (seq (.getHeader bp "Content-Type"))))
      (t/is (= "project.clj" (.getFileName bp)))
      (t/is (= "attachment" (.getDisposition bp)))))

  (t/testing "attachment by path string"
    (let [part {:type :attachment :content "project.clj"}
          bp (sut/make-bodypart part "UTF-8")]
      (t/is (instance? MimeBodyPart bp))
      (t/is (= ["text/x-clojure"] (seq (.getHeader bp "Content-Type"))))
      (t/is (= "project.clj" (.getFileName bp)))
      (t/is (= "attachment" (.getDisposition bp))))))

(t/deftest make-bodypart-with-content-id-test
  (let [part {:type :inline :content (io/file "project.clj") :id "foo-id"}
        bp (sut/make-bodypart part "UTF-8")]
    (t/is (instance? MimeBodyPart bp))
    (t/is (= "<foo-id>" (.getContentID bp)))))
