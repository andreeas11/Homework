function out = bilinear_rotate_RGB(img, rotation_angle)
    % =========================================================================
    % Aplica interpolare bilineara pentru a roti o imagine RGB cu un unghi dat.
    % =========================================================================
    
    % TODO: extrage canalul rosu al imaginii
    rosu = img(:,:,1);

    % TODO: extrage canalul verde al imaginii
    verde = img(:,:,2);

    % TODO: extrace canalul albastru al imaginii
    albastru = img(:,:,3);

    % TODO: aplică rotația pe fiecare canal al imaginii
    outRosu = bilinear_rotate(rosu, rotation_angle);
    outVerde = bilinear_rotate(verde, rotation_angle);
    outAlbastru = bilinear_rotate(albastru, rotation_angle);

    % TODO: reconstruiește imaginea RGB finala (hint: cat)
    [m, n] = size(outRosu);
    out = zeros(m, n, 3);
    
    out (:, :, 1) = outRosu;
    out (:, :, 2) = outVerde;
    out (:, :, 3) = outAlbastru;
    
    out = uint8(out);
endfunction