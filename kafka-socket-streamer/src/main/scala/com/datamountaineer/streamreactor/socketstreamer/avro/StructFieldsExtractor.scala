/**
  * Copyright 2016 Datamountaineer.
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  **/

package com.datamountaineer.streamreactor.socketstreamer.avro

import org.apache.avro.generic.GenericRecord
import org.apache.avro.util.Utf8

import scala.collection.JavaConversions._

case class RecordData(timestamp: Long, fields: Seq[(String, Any)])

trait FieldsValuesExtractor {
  def get(record: GenericRecord): Map[String, Any]
}

case class GenericRecordFieldsValuesExtractor(includeAllFields: Boolean,
                                              fieldsAliasMap: Map[String, String]) extends FieldsValuesExtractor {

  def get(record: GenericRecord): Map[String, Any] = {
    val fields = record.getSchema.getFields
      .filter(f => includeAllFields || fieldsAliasMap.contains(f.name.toUpperCase()))

    fields.map { field =>
      val fieldName = field.name
      val value = record.get(fieldName) match {
        case utf: Utf8 => new String(utf.getBytes)
        case other => other
      }

      fieldsAliasMap.getOrElse(fieldName.toUpperCase, fieldName) -> value
    }.toMap
  }
}


