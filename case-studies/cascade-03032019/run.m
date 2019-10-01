function prog_vect=Jon_Bigboard_Repressilator(prog_vect,FPGA_lib,FPGA_xptr)

% 150p, 100n, 150p, 400n, 150p, 1u, 150p
% IStotSweep(1,:) = digital_current_to_closest_bits('B00',100,1);
%
% numPoints=length(IStotSweep(:,1));
% initSamples = 23;
% numSamples=500;
% Imeas=zeros(initSamples+numPoints*numSamples,7); %%%%%%%%%%%%%%%%%%%%%%% note!

%% ADC
% Scale factor from ADC output to analog current (when clk=5MHz)
ADC_scale=calibrated('ADC','Aug15',28); %5Mhz

% program ADC scale factors in the FPGA
scalefactors = [40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40 40];
chip_addr = 0;
DNAChip_program_chip_FPGA_initparam_aug15(FPGA_lib,FPGA_xptr,scalefactors,chip_addr);

% figure(1); clf; figure(2); clf;
% options={'o-','x-','+-','*-'};

%% set SRAM connection
%DNAChip_reset_sram_aug15_bigboard_repressilator_prtnchip(FPGA_lib,FPGA_xptr);
tstart=tic; % setting the start point for elapsed time measurement

in_all=repressilator_sram();

disp('Programming connection SRAM...')
pause(1);

%program_FPGA_sram_bigboard(FPGA_lib,FPGA_xptr,sram_data_prtn,sram_data_gene)
sram_data_prtn=in_all;
for i=0:1
    chip_addr = i;
    program_FPGA(sram_data_prtn,chip_addr,FPGA_lib,FPGA_xptr,'prtn','sram');
    %program_FPGA_trigger(FPGA_lib,FPGA_xptr);
    %DNAChip_program_chip_FPGA_sram_aug15(FPGA_lib,FPGA_xptr,in_all,chip_addr);
end

disp('Programming chips using FPGA complete')

elapsedtime=toc(tstart); % read elapsed time
disp(['SRAM programming took ' num2str(elapsedtime) ' seconds to run.'])

% set simulation values
% recommended clk freq: 5MHz
disp('Loading simulation parameters..')
pause(1);

%% Initial Conditions
prog_vect=load_sr_initial(prog_vect);
for i=0:9
    program_FPGA(prog_vect,i,FPGA_lib,FPGA_xptr,'prtn','sr');
end

disp('Setting initial conditions.')
pause(5);

%% reset FIFO for test
% Run this before adding new prog_vects
FIFO_reset(FPGA_xptr);

%% Start Simulation
prog_vect=load_sr_initial(prog_vect);
for i=0:9
    program_FPGA(prog_vect,i,FPGA_lib,FPGA_xptr,'prtn','sr');
end

%% reset FIFO for test
% Run this before adding new prog_vects
FIFO_reset(FPGA_xptr);

numRead = 10;%Jon edited to current value %40 % 20=1.92s. 200=19.2s
numOutputVar1 = 12; numOutputVar2 = 12;
%readarray = DNAChip_read_chip_FPGA_aug15(FPGA_lib,FPGA_xptr,numRead,numOutputVar1,numOutputVar2,ADCOUTtoCurrent_chip3,ADCOUTtoCurrent_chip2);
raw = read_chip_FPGA(FPGA_lib,FPGA_xptr,numRead,numOutputVar1,numOutputVar2);
for i=[1:12]
    readarray(:,i)=polyval(ADC_scale(:,i),raw(:,i));
end
assignin('base','readarray',readarray);
assignin('base','raw',raw);

%% Plot
%figure(12);
close all

figure
hold on
time=[1:size(readarray)]*0.002;
for i= [1,2,3]
    plot(time,readarray(:,i),'linewidth',1.5)
end
xlabel('Time (s)');
ylabel('Proteins per cell (nA)');
legend('Protein1','Protein2','Protein3','Location','Northwest')

figure
hold on
time=[1:size(readarray)]*0.002;
for i= [4,5,6]
    plot(time,readarray(:,i),'linewidth',1.5)
end
xlabel('Time (s)');
ylabel('mRNA per cell (nA)');
legend('mRNA1','mRNA2','mRNA3','Location','Northwest')

end

function in_all=repressilator_sram()
  load('sram.mat','in_all');
  assert(size(in_all,1) == 1024);
  assert(size(in_all,2) == 100);
end

function prog_vect=load_sr_initial(prog_vect)
  load('sr_initial.mat','prog_vect');
  assert(size(prog_vect,1) == 350);
  assert(size(prog_vect,2) == 4);
end

function prog_vect=load_sr(prog_vect)
  load('sr.mat','prog_vect');
  assert(size(prog_vect,1) == 350);
  assert(size(prog_vect,2) == 4);
end
