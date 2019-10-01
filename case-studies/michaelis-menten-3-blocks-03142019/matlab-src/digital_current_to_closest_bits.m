function ItoBits=digital_current_to_closest_bits(inputname,ADCOUT,scalefactor)

ItoBits = zeros(1,7); % bits to store the value of current

current = ADCOUT * scalefactor; % order of nA
fprintf(['[log] ' inputname ': desired I = ' num2str(current,3) 'nA, ']);

Iref_10u = 20000/2; % based on measured value. maybe change to 22000?

% clamp to max (10uA) or min (Iref_10u/2^17 ~76.3pA) current
% min current should not be 0 to prevent slow DAC operation
if current == -99
    fprintf('zero. ');
    current = 0;
elseif current > 19000
    fprintf('but I > 19uA! Clamped to 19uA. ');
    current = 19000;
elseif current < Iref_10u / 2^17
    fprintf([', but I < Imin! Clamped to Imin (' num2str(Iref_10u/2^17,3) 'nA). ']);
    current = Iref_10u / 2^17;
end

LSBcurrents = [Iref_10u/2^17 Iref_10u/2^15 Iref_10u/2^13 Iref_10u/2^11 Iref_10u/2^9 Iref_10u/2^7 Iref_10u/2^5 Iref_10u/2^3]; % define this as global to make the program faster?
% also Iref is not 20u but ~22u. Multiply above currents by 22/20 to increase accuracy?
boundary = LSBcurrents * 15.5;

for i = 1:8
    if current < boundary(i)
        ItoBits(5:7)=de2bi(i-1,3); % set range
        current = current / LSBcurrents(i); % less than LSBcurrents * 15.5
        ItoBits(1:4)= de2bi(round(current),4); % set value
        fprintf(['closest I = ' num2str(round(current)*LSBcurrents(i),3) 'nA\n']);
        break
    end
end

end