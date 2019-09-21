function graphApproaches()
   M1 = dlmread("./compareall0.000011556692034603");
   x = 0:0.00001:5;
   hold on;
   scatter(x, M1(2,:), 'x');
   scatter(x, M1(3,:), '+');
    scatter(x, M1(4,:), 'o');
   plot(x, M1(1,:));
   legend({"Verlet approach", "Beeman approach", "Gear Corrector-Predictor", "Analitical solution"}, "location", "southeast");
   
endfunction
