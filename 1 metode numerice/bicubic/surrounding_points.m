function [x1 y1 x2 y2] = surrounding_points(n, m, x, y)
    % =========================================================================
    % Calculeaza cele 4 puncte ce contin in interior (x, y)
    % Primeste si dimensiunea unei imagini m x n, pentru a asigura ca nu se
    % iese din matrice.
    % =========================================================================
    
    % TODO: Calculeaza x1, y1, x2, y2
    x1 = floor(x);
    y1 = floor(y);
    x2 = floor(x)+1;
    y2 = floor(y)+1;

    % TODO: daca y se afla pe ultima linie, asigura ca y2 nu o sa iasa din
    % matrice (nu o sa fie m + 1 daca y1 = m)
    if y > (m - 1)
      y1 = m-1;
      y2 = m;
    endif

    % TODO: analog daca x se afla pe ultima coloana
    if x > (n-1)
      x1 = n-1;
      x2 = n;
    endif

endfunction