g = sprintf('Number of features: ');
% Obtain the number of features
n_features = input(g);

g = sprintf('Number of significant features: ');
% Obtain the number of significant features
n_significant = input(g);

g = sprintf('Number of clusters: ');
% Obtain the number of clusters
n_clusters = input(g);

disp(sprintf('Select the position of significant features within vector data:'));
for i = 1:n_significant
    g = sprintf('Significant feature %d position (1..%d): ', i, n_features);
    % Obtain the position of significant features
    significant_pos(i) = input(g);  
end

disp(sprintf('Enter the coordinates of the %d clusters. Use range [0..1]: ', n_clusters));
centroids(1:n_clusters,1:n_features)=0;
for i = 1:n_clusters
    for j = 1:n_significant
        g = sprintf('Cluster: %d, feature %d: ',i,significant_pos(j));
        
        coordinate=input(g);
        while (coordinate < 0) || (coordinate > 1)
            disp(sprintf('Coordinate %d is not in range [0..1]: ', coordinate));
            coordinate=input(g);
        end
        centroids(i,significant_pos(j))=coordinate;
    end
end

g = sprintf('Number of patterns: ');
% Number of patterns in the benchmark file
n_patterns = input(g);

radius = 0.7 / n_clusters;
for i = 1:n_patterns
    for j = 1:n_features
        dataset(i,j)=rand(1);
    end
    
    random_cluster = floor(n_clusters*rand(1))+1;
    for k = 1:n_significant
        coordinate = centroids(random_cluster,significant_pos(k)) + (rand(1)-0.5)*radius;
        while (coordinate < 0) || (coordinate > 1)
            coordinate = centroids(random_cluster,significant_pos(k)) + (rand(1)-0.5)*radius;
        end
        dataset(i,significant_pos(k))=coordinate;
    end
end

dlmwrite('dataset.data',dataset,'delimiter','\t','precision','%.6f');

fileID = fopen ('dataset.info','w');
fprintf(fileID,'Synthetic dataset\n\n');
fprintf(fileID,'Number of features: %d\n', n_features);
fprintf(fileID,'Significant features:');

for i = 1:n_significant
    fprintf(fileID,' %d', significant_pos(i));
end

fprintf(fileID,'\nNumber of clusters: %d\n', n_clusters);

fprintf(fileID,'\nCentroids of the clusters:\n');

for i = 1:n_clusters
    fprintf(fileID,'Cluster %d: (\t', i);
    for j = 1:n_features
        if sum(j == significant_pos)
                fprintf(fileID, '%.6f', centroids(i,j)');
        else
                fprintf(fileID, '-');
        end
        fprintf(fileID,'\t');
    end
    fprintf(fileID,')\n');
end

fprintf(fileID,'\nNumber of patterns: %d\n', n_patterns);
fclose(fileID);
