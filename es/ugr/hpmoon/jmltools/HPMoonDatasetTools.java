/**
 * This file is part of the HPMoon Project Library
 * 
 * Copyright (c) 2016, Jesús González
 * 
 */

package es.ugr.hpmoon.jmltools;

import java.util.ArrayList;
import java.util.List;

import ec.util.MersenneTwister;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.DatasetTools;

public class HPMoonDatasetTools {	
	/**
	 * Generates a new dataset from the input data as a projection of some selected features according to mask
	 * The length of mask should match the number of attributes in the dataset.
	 * Otherwise, the smaller value between the number of features in the dataset and the
	 * length of the mask will be used as the maximum number of selectable features 
	 * @param data Input dataset
	 * @param mask Mask to select the features in the new dataset
	 * @return A new dataset containing only the features selected by the mask
	 */
	public static Dataset project (Dataset data, boolean [] mask)
	{
		/* Indexes of the attributes to be selected according to the mask */
		List<Integer> indexes = new ArrayList<Integer>();
		for (int i=0 ; i<Math.min(data.noAttributes(), mask.length) ; i++)
			if (mask[i])
				indexes.add(i);
					
		/* Create an empty dataset */
		Dataset outData = new DefaultDataset();
		
		/* For every instance in the dataset */
		double [] selectedAttrs = new double[indexes.size()];
		for (Instance inputIns : data)
		{
			for (int j=0 ; j<selectedAttrs.length ; j++)
				selectedAttrs[j] = inputIns.value(indexes.get(j));
			
			outData.add(new DenseInstance(selectedAttrs, inputIns.classValue()));
		}

		/* Return a dataset containing only the selected features */
		return outData;
	}

	/**
	 * Normalizes all the attributes of a dataset in the range [0..1] 
	 * @param data The dataset
	 */
	public static void normalize (Dataset data)
	{
		Instance minAttrs = DatasetTools.minAttributes(data);
		for (int i = 0 ; i<data.size() ; i++)
		{
			Instance current = data.instance(i);
			Instance modified = current.minus(minAttrs);
			modified.setClassValue(current.classValue());
			data.set(i, modified);
		}

		Instance maxAttrs = DatasetTools.maxAttributes(data);
		for (int i = 0 ; i<data.size() ; i++)
		{
			Instance current = data.instance(i);
			Instance modified = current.divide(maxAttrs);
			modified.setClassValue(current.classValue());
			data.set(i, modified);
		}
	}

	/**
	 * Appends some extra random attributes to each sample in the dataset.
	 * @param data        The dataset
	 * @param nExtraAttrs Number of random extra attributes to be appended 
	 */
	public static void appendRandomAttributes (Dataset data, int nExtraAttrs)
	{					
		/* Random generator */
		MersenneTwister rg = new MersenneTwister(System.currentTimeMillis());
		
		/* Create an empty dataset */
		int nAttrs = data.noAttributes();
		
		for (int i = 0 ; i<data.size() ; i++)
		{
			Instance current = data.instance(i);

			double [] newSample = new double [nAttrs + nExtraAttrs];
			for (int j=0 ; j<nAttrs ; j++)
				newSample[j] = current.value(j);

			for (int j=nAttrs ; j<nAttrs+nExtraAttrs ; j++)
				newSample[j] = rg.nextDouble();
			
			data.set(i, new DenseInstance(newSample, current.classValue()));
		}
	}

}
