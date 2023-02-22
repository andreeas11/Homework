function out = nn_2x2_RGB(img, STEP = 0.1)
    % =========================================================================
    % Aplica interpolare nearest neighbour pe imaginea 2x2 img cu puncte
    % intermediare echidistante.
    % img este o imagine colorata RGB.
    % =========================================================================

    % TODO: extrage canalul rosu al imaginii
    rosu = img(:,:,1);

    % TODO: extrage canalul verde al imaginii
    verde = img(:,:,2);

    % TODO: extrace canalul albastru al imaginii
    albastru = img(:,:,3);

    % TODO: aplica functia nn pe cele 3 canale ale imaginii
    outRosu = nn_2x2(rosu, STEP);
    outVerde = nn_2x2(verde, STEP);
    outAlbastru = nn_2x2(albastru, STEP);

    % TODO: formeaza imaginea finala cu cele 3 canale de culori
    % Hint: functia cat
    [m, n] = size(outRosu);
    out = zeros(m, n, 3);
    
    out (:, :, 1) = outRosu;
    out (:, :, 2) = outVerde;
    out (:, :, 3) = outAlbastru;
    
    out = uint8(out);
    
endfunction

