function meanQuadraticErrors()
  M1 = dlmread("./target/quadraticDifference1556859192787");
  hold on;
  scatter(M1(4,:), M1(1,:), 'filled');
  scatter(M1(4,:), M1(2,:), 'filled');
  scatter(M1(4,:), M1(3,:), 'filled');
  set(gca,'xscale','log');
  set(gca,'yscale','log');
  legend({"Verlet approach", "Beeman approach", "Gear Corrector-Predictor"}, "location", "southeast");
endfunction