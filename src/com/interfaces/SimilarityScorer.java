package com.interfaces;

import com.model.Sentence;

public interface SimilarityScorer
{
	public double similarity(Sentence a, Sentence b);
}
