package edu.jhu.hlt.concrete.miscommunication.comms;

import org.inferred.freebuilder.FreeBuilder;

import edu.jhu.hlt.concrete.Communication;
import edu.jhu.hlt.concrete.miscommunication.NonEmptyString;

/**
 * {@link Communication} whose {@link Communication#getText()} is not null or empty
 */
@FreeBuilder
public abstract class TextCommunication {
  public abstract String getText();
  public abstract JavaCommunication getCommunication();

  public static TextCommunication convert(Communication comm) {
    JavaCommunication jc = JavaCommunication.convert(comm);
    return new Builder()
        .setText(comm.getText())
        .setCommunication(jc)
        .build();
  }

  static class Builder extends TextCommunication_Builder {
    Builder() {

    }

    @Override
    public Builder setText(String txt) {
      NonEmptyString.create(txt);
      return super.setText(txt);
    }
  }
}
