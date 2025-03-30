package com.exclamationlabs.connid.base.edition.neo.model;

public enum ConnIdType {
  UID, // __UID__ unique identifier understood by the ConnID framework
  NAME, // __NAME__ unique name understood by the ConnID framework
  NONE // Any other attribute that is neither __UID__ nor __NAME__
}
