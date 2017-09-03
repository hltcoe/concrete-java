package edu.jhu.hlt.concrete.ingesters.webposts;

import com.beust.jcommander.ParametersDelegate;

import edu.jhu.hlt.concrete.ingesters.base.IngesterParameterDelegate;

class Opts {
  @ParametersDelegate
  IngesterParameterDelegate delegate = new IngesterParameterDelegate();
}