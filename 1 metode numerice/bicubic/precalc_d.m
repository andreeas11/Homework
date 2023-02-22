function [Ix, Iy, Ixy] = precalc_d(I)
    % =========================================================================
    % Prealculeaza matricile Ix, Iy si Ixy ce contin derivatele dx, dy, dxy ale 
    % imaginii I pentru fiecare pixel al acesteia
    % =========================================================================
    
    % obtine dimensiunea imaginii
    [m n nr_colors] = size(I);
    
    % TODO: fa cast matricii I la double
    I = double(I);
    Ix = zeros(m, n);
    Ix = double(Ix);
    Iy = zeros(m, n);
    Iy = double(Iy);
    Ixy = zeros(m, n);
    Ixy = double(Ixy);

    % TODO: calculeaza matricea cu derivate fata de x Ix
    for y = 1 : m
       for x = 2 : (n - 1)
          Ix(y, x) = fx(I, x, y);
       end
    end

    % TODO: calculeaza matricea cu derivate fata de y Iy
    for y = 2 : (m - 1)
       for x = 1 : n
          Iy(y, x) = fy(I, x, y);
       end
    end

    % TODO: calculeaza matricea cu derivate fata de xy Ixy
    for y = 2 : (m - 1)
       for x = 2 : (n - 1)
          Ixy(y, x) = fxy(I, x, y);
       end
    end

endfunction
